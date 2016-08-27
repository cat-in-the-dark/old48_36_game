package com.catinthedark.models

import com.catinthedark.lib.network.messages.Message

case class HelloMessage(name: String) extends Message
case class ServerHelloMessage(clientId: String) extends Message