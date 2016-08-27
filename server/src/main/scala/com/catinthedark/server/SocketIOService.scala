package com.catinthedark.server

import java.util.UUID
import java.util.concurrent.{ConcurrentHashMap, Executors, TimeUnit}

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

  private val room = Room(UUID.randomUUID())
  private val players = new ConcurrentHashMap[UUID, Player]()

  server.addConnectListener(new ConnectListener {
    override def onConnect(client: SocketIOClient): Unit = {
      log.info(s"New connection ${client.getSessionId} of ${server.getAllClients.size()}")
    }
  })

  server.addEventListener(MESSAGE, classOf[String], new DataListener[String] {
    override def onData(client: SocketIOClient, data: String, ackSender: AckRequest): Unit = {
//      val wrapper = mapper.readValue(data, classOf[JacksonConverter.Wrapper])
      val player = players.get(client.getSessionId)
      if (player == null) {
        onNewPlayer(client, data)
      } else {

      }
    }
  })

  server.addDisconnectListener(new DisconnectListener {
    override def onDisconnect(client: SocketIOClient): Unit = {
      log.info(s"Disconnected ${client.getSessionId}")
      val player = players.get(client.getSessionId)
      if (player != null && player.room.disconnect(client)) {
        // we can send message to clients that user is disconnected
      }
    }
  })

  def onNewPlayer(client: SocketIOClient, data: String): Unit = {
    val room = findOrCreateRoom()
    val player = Player(room, client, null)
    room.connect(player)
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
