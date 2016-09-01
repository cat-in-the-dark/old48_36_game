package com.catinthedark.models

import com.catinthedark.lib.network.Message

sealed class GameMessage extends Message
case class EnemyDisconnectedMessage(clientId: String) extends Message
case class GameStartedMessage(clientId: String) extends Message
case class RoundEndsMessage(gameStateModel: GameStateModel) extends Message
case class HelloMessage(name: String) extends Message
case class ServerHelloMessage(clientId: String) extends Message
case class MoveMessage(speedX: Float, speedY: Float, angle: Float, stateName: String) extends Message
case class GameStateMessage(gameStateModel: GameStateModel) extends Message
case class SoundMessage(soundName: String) extends Message
case class ThrowBrickMessage(x: Float, y: Float, force: Float, angle: Float) extends Message
