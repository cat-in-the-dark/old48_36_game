package com.catinthedark.ld36.network

import java.net.URI

import com.catinthedark.ld36.Assets
import com.catinthedark.lib.network.{JacksonConverterScala, Message, MessageBus, SocketIOTransport}
import com.catinthedark.models._

class NetworkWSControl(val serverAddress: URI) extends NetworkControl {
  private val messageConverter = new JacksonConverterScala()
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

  messageBus.subscribe(classOf[SoundMessage], (message: SoundMessage, sender: String) => {
    println(s"sound event $message")
    val snd = SoundNames.withName(message.soundName)
    Assets.Audios.soundMap(snd).play(0.6f)
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
