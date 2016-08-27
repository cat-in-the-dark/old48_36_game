package com.catinthedark.models

sealed class Message
case class DisconnectedMessage(clientId: String) extends Message
case class GameStartedMessage(clientId: String) extends Message
case class HelloMessage(name: String) extends Message
case class ServerHelloMessage(clientId: String) extends Message
case class MoveMessage(speedX: Float, speedY: Float, angle: Float, stateName: String)
