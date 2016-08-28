package com.catinthedark.ld36.network

import java.net.URI

import com.catinthedark.lib.network.{JacksonConverterScala, MessageBus, SocketIOTransport}
import com.catinthedark.models._
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

class NetworkWSControl(val serverAddress: URI) extends NetworkControl {
  private val objectMapper = new ObjectMapper()
  objectMapper.registerModule(DefaultScalaModule)
  private val messageConverter = new JacksonConverterScala(objectMapper)
  private val transport = new SocketIOTransport(messageConverter, serverAddress)
  private val messageBus = new MessageBus(transport)

  MessageConverter.registerConverters(messageConverter)

  messageBus.subscribe(classOf[GameStartedMessage], (message: GameStartedMessage, sender: String) => {
    isConnected = Some()
    onGameStarted(message.clientId)
  })

  messageBus.subscribe(classOf[EnemyDisconnectedMessage], (message: EnemyDisconnectedMessage, sender: String) => {
    println(s"onEnemyDisconnected $message")
    onEnemyDisconnected()
  })

  messageBus.subscribe(classOf[ServerHelloMessage], (message: ServerHelloMessage, sender: String) => {
    println(s"onServerHelloMessage $message")
    onServerHello()
  })

  messageBus.subscribe(classOf[GameStateMessage], (message: GameStateMessage, sender: String) => {
    onGameState(message.gameStateModel)
  })

  messageBus.subscribe(classOf[RoundEndsMessage], (message: RoundEndsMessage, sender: String) => {
    println(s"Round ends $message")
    onRoundEnds(message.gameStateModel)
  })

  override def run(): Unit = {
    transport.connect()
  }

  override def dispose(): Unit = {
    super.dispose()
    transport.disconnect()
  }

  override def processOut(message: Message): Unit = {
    messageBus.send(message)
  }
}
