package com.catinthedark.server

import java.util.UUID
import java.util.concurrent.TimeUnit

import com.catinthedark.common.Const
import com.catinthedark.lib.Intervals
import com.catinthedark.lib.network.JacksonConverterScala
import com.catinthedark.models._
import com.catinthedark.server.models.Room
import com.catinthedark.server.persist.{IRepository, Repository}
import com.corundumstudio.socketio._
import com.corundumstudio.socketio.listener.{ConnectListener, DataListener, DisconnectListener}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule
import org.slf4j.LoggerFactory

import scala.collection.JavaConversions._

class SocketIOService {
  private val gameTick = Config.gameTick
  private val log = LoggerFactory.getLogger(classOf[SocketIOService])
  private val config = new Configuration
  config.setPort(Config.port)
  private val socketConfig = new SocketConfig
  socketConfig.setReuseAddress(true)
  config.setSocketConfig(socketConfig)
  private val server = new SocketIOServer(config)
  private val mapper = new ObjectMapper()
  mapper.registerModule(DefaultScalaModule)
  private val converter = new JacksonConverterScala(mapper)
  MessageConverter.registerConverters(converter)
  private val executor = new Intervals(4)

  private val geoIPService = new GeoIPService(mapper)
  private val repository: IRepository = new Repository(geoIPService, mapper)
  private val room = Room(UUID.randomUUID(), converter, repository)

  server.addConnectListener(new ConnectListener {
    override def onConnect(client: SocketIOClient): Unit = {
      log.info(s"New connection ${client.getSessionId} of ${server.getAllClients.size()}")
      val msg = ServerHelloMessage(client.getSessionId.toString)
      client.sendEvent(EventNames.MESSAGE, converter.toJson(msg))
    }
  })

  server.addEventListener(EventNames.MESSAGE, classOf[String], new DataListener[String] {
    override def onData(client: SocketIOClient, data: String, ackSender: AckRequest): Unit = {
      val wrapper = converter.fromJson(data)
      wrapper.data match {
        case msg: HelloMessage =>
          onNewPlayer(client, msg.name)
        case msg: MoveMessage =>
          room.onMove(client, msg)
        case msg: ThrowBrickMessage =>
          room.onThrow(client, msg)
        case _ => log.warn("Undefined msg!!!!!")
      }
    }
  })

  server.addDisconnectListener(new DisconnectListener {
    override def onDisconnect(client: SocketIOClient): Unit = {
      log.info(s"Disconnected ${client.getSessionId}")
      val msg = converter.toJson(EnemyDisconnectedMessage(client.getSessionId.toString))
      room.disconnect(client)
      room.players.values().iterator.foreach(p => {
        p.socket.sendEvent(EventNames.MESSAGE, msg)
      })
      room.checkTimer()
      log.info(s"Remaining players: ${room.players.values().map(_.entity.name).mkString(", ")}")
    }
  })

  def onNewPlayer(client: SocketIOClient, playerName: String): Unit = {
    val room = findOrCreateRoom()
    if (room.spawnPlayer(client, playerName)) {
      val gsm = GameStartedMessage(client.getSessionId.toString)
      client.sendEvent(EventNames.MESSAGE, converter.toJson(gsm))
    } else {
      log.warn("Room is full!!")
    }
  }

  def findOrCreateRoom(): Room = {
    room
  }

  def registerGameTimer(): Unit = {
    executor.repeat(gameTick, TimeUnit.MILLISECONDS, room.onTick)
    executor.repeat(Const.Balance.bonusDelay, TimeUnit.SECONDS, room.spawnBonus)
    executor.repeat(1, TimeUnit.SECONDS, room.timerTick)
  }

  def start(): Unit = {
    server.start()
    registerGameTimer()
    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
      override def run(): Unit = {
        server.stop()
      }
    }))
  }
}
