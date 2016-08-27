package com.catinthedark.models

import com.catinthedark.lib.network.messages.Message

case class MoveMessage(x: Float, y: Float, angle: Float, idle: Boolean) extends Message
case class JumpMessage(x: Float, y: Float, angle: Float, scale: Float) extends Message
case class ShootMessage(x: Float, y: Float, shotObject: String) extends Message

case class HelloMessage(name: String) extends Message
case class ServerHelloMessage(clientId: String) extends Message