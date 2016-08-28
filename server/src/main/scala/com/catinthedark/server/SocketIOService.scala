package com.catinthedark.server

import java.util.UUID
import java.util.concurrent.{Executors, TimeUnit}

import com.catinthedark.common.Const
import com.catinthedark.lib.network.JacksonConverterScala
import com.catinthedark.models._
import com.catinthedark.server.models.Room
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
  private val executor = Executors.newScheduledThreadPool(4)

  private val room = Room(UUID.randomUUID(), converter)

  server.addConnectListener(new ConnectListener {
    override def onConnect(client: SocketIOClient): Unit = {
      log.info(s"New connection ${client.getSessionId} of ${server.getAllClients.size()}")
      val msg = ServerHelloMessage(client.getSessionId.toString)
      val data = converter.toJson(msg)
      println(s"SEND: $data")
      client.sendEvent(EventNames.MESSAGE, data)
    }
  })

  server.addEventListener(EventNames.MESSAGE, classOf[String], new DataListener[String] {
    override def onData(client: SocketIOClient, data: String, ackSender: AckRequest): Unit = {
      val wrapper = converter.fromJson(data)
      wrapper.data match {
        case msg: HelloMessage =>
          log.info(s"GET: $data")
          val player = onNewPlayer(client, msg.name)
          val gsm = GameStartedMessage(client.getSessionId.toString)
          val response = converter.toJson(gsm)
          log.info(s"SEND: $response")
          client.sendEvent(EventNames.MESSAGE, response)
        case msg: MoveMessage =>
          room.onMove(client, msg)
        case _ => log.warn("Undefined msg!!!!!")
      }
    }
  })

  server.addDisconnectListener(new DisconnectListener {
    override def onDisconnect(client: SocketIOClient): Unit = {
      log.info(s"Disconnected ${client.getSessionId}")
      val msg = converter.toJson(EnemyDisconnectedMessage(client.getSessionId.toString))
      log.info(s"SEND: $msg")
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
    room.connect(room.spawnPlayer(client, playerName))
  }

  def findOrCreateRoom(): Room = {
    room
  }

  def registerGameTimer(): Unit = {
    executor.scheduleWithFixedDelay(new Runnable {
      override def run(): Unit = {
        room.onTick()
      }
    }, 0, gameTick, TimeUnit.MILLISECONDS)

    executor.scheduleWithFixedDelay(new Runnable {
      override def run(): Unit = {
        room.checkTimer()
        if (room.timeRemains > 0) {
          room.timeRemains -= 1
        } else {
          room.finishRound()
        }
      }
    }, 0, 1, TimeUnit.SECONDS)

    executor.scheduleWithFixedDelay(new Runnable {
      override def run(): Unit = {
        room.spawnBonus()
      }
    }, Const.Balance.bonusDelay, Const.Balance.bonusDelay, TimeUnit.SECONDS)
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
