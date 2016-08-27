package com.catinthedark.lib.network

import com.catinthedark.models.Message

trait IMessageBus {
  def send(message: Message): Unit

  def subscribe[T](clazz: Class[T], callback: (T, String) => Unit)
}

case class Wrapper(data: Any, className: String, sender: String)

trait Transport {
  def send(message: Message): Unit
  def setReceiver(receiver: (Wrapper) => Unit): Unit
}