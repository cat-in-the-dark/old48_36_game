package com.catinthedark.server

import java.util.UUID
import java.util.concurrent.{ConcurrentHashMap, Executors, TimeUnit}
import java.util.function.BiConsumer

import com.catinthedark.server.models.{Player, Room}
import com.corundumstudio.socketio._
import com.corundumstudio.socketio.listener.{ConnectListener, DataListener, DisconnectListener}
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory

class SocketIOService {
  private val gameTick = Config.gameTick
  private val MESSAGE = "message"
  private val log = LoggerFactory.getLogger(classOf[SocketIOService])
  private val config = new Configuration
  config.setPort(Config.port)
  private val socketConfig = new SocketConfig
  socketConfig.setReuseAddress(true)
  config.setSocketConfig(socketConfig)
  private val server = new SocketIOServer(config)
  private val mapper = new ObjectMapper()
  private val executor = Executors.newScheduledThreadPool(4)

  private val rooms = new ConcurrentHashMap[UUID, Room]()
  private val players = new ConcurrentHashMap[UUID, Player]()

  server.addConnectListener(new ConnectListener {
    override def onConnect(client: SocketIOClient): Unit = {
      log.info(s"New connection ${client.getSessionId} of ${server.getAllClients.size()}")
    }
  })

  server.addEventListener(MESSAGE, classOf[String], new DataListener[String] {
    override def onData(client: SocketIOClient, data: String, ackSender: AckRequest): Unit = {
//      val wrapper = mapper.readValue(data, classOf[JacksonConverter.Wrapper])
    }
  })

  server.addDisconnectListener(new DisconnectListener {
    override def onDisconnect(client: SocketIOClient): Unit = {
      log.info(s"Disconnected ${client.getSessionId}")
    }
  })

  def registerGameTimer(): Unit = {
    executor.scheduleWithFixedDelay(new Runnable {
      override def run(): Unit = {
        rooms.forEach(new BiConsumer[UUID, Room] {
          override def accept(id: UUID, room: Room): Unit = {
            room.onTick()
          }
        })
      }
    }, 0, gameTick, TimeUnit.MILLISECONDS)
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
