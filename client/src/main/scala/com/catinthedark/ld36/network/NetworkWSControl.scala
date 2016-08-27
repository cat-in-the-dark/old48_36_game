package com.catinthedark.ld36.network

import java.net.URI

import com.catinthedark.lib.network.IMessageBus.Callback
import com.catinthedark.lib.network.{JacksonConverterScala, MessageBus, SocketIOTransport}
import com.catinthedark.models.{DisconnectedMessage, GameStartedMessage, MessageConverter, ServerHelloMessage}
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

class NetworkWSControl(val serverAddress: URI) extends NetworkControl {
  private val objectMapper = new ObjectMapper()
  objectMapper.registerModule(DefaultScalaModule)
  private val messageConverter = new JacksonConverterScala(objectMapper)
  private val transport = new SocketIOTransport(messageConverter, serverAddress)
  private val messageBus = new MessageBus(transport)
  
  MessageConverter.registerConverters(messageConverter)

  messageBus.subscribe(classOf[GameStartedMessage], new Callback[GameStartedMessage] {
    override def apply(message: GameStartedMessage, sender: String): Unit = {
      isConnected = Some()
      onGameStarted(message.clientId)
    }
  })
  
  messageBus.subscribe(classOf[DisconnectedMessage], new Callback[DisconnectedMessage] {
    override def apply(message: DisconnectedMessage, sender: String): Unit = {
      println(s"onEnemyDisconnected $message")
      onEnemyDisconnected()
    }
  })

  messageBus.subscribe(classOf[ServerHelloMessage], new Callback[ServerHelloMessage] {
    override def apply(message: ServerHelloMessage, sender: String): Unit = {
      println(s"onServerHelloMessage $message")
      onServerHello()
    }
  })
  
  override def run(): Unit = {
    transport.connect()
  }
  
  override def dispose(): Unit = {
    super.dispose()
    transport.disconnect()
  }

  override def processOut(message: Any): Unit = {
    messageBus.send(message)
  }
}
