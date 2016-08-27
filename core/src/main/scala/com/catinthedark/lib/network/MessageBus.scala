package com.catinthedark.lib.network

import com.catinthedark.models.Message

import scala.collection.mutable

class MessageBus(val transport: Transport) extends IMessageBus {
  val subscribers = new mutable.ListBuffer[Subscriber[_]]
  transport.setReceiver(wrapper => {
    subscribers.filter((sub) => {
        sub.className == wrapper.data.getClass.getCanonicalName
      }).foreach((sub) => {
        sub.send(wrapper.data, wrapper.sender)
      })
  })

  override def send(message: Message): Unit = {
    transport.send(message)
  }

  override def subscribe[T](clazz: Class[T], callback: (T, String) => Unit): Unit = {
    subscribers += Subscriber(clazz.getCanonicalName, callback)
  }
}

case class Subscriber[T](className: String, callback: (T, String) => Unit) {
  def send(data: Any, sender: String): Unit = {
    callback(data.asInstanceOf[T], sender)
  }
}