package com.catinthedark.lib.network

import com.catinthedark.models.Message

abstract class NetworkTransport(val converter: Converter) extends Transport {
  var receiver: (Wrapper) => Unit = _

  override def send(message: Message): Unit = {
    try {
      val json = converter.toJson(message)
      sendToNetwork(json)
    } catch {
      case e: Exception => e.printStackTrace(System.err)
    }
  }

  def onConnect(): Unit ={

  }

  def onDisconnect(): Unit ={

  }

  def onConnectionError(): Unit ={

  }

  def onReceive(json: String): Unit = {
    try {
      val data = converter.fromJson(json)
      receiver(data)
    } catch {
      case e: Exception => e.printStackTrace(System.err)
    }
  }

  override def setReceiver(receiver: (Wrapper) => Unit): Unit = {
    this.receiver = receiver
  }

  def sendToNetwork(msg: String)
}

trait Converter {
  def toJson(data: Message): String
  def fromJson(json: String): Wrapper
}