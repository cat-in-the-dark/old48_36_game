package com.catinthedark.lib.network

import java.net.URI

import io.socket.client.{IO, Socket}
import io.socket.emitter.Emitter.Listener

class SocketIOTransport(override val converter: Converter, val uri: URI) extends NetworkTransport(converter) {
  val options = new IO.Options()
  options.forceNew = true
  options.reconnection = true

  val socket = IO.socket(uri, options)

  socket
    .on(Socket.EVENT_CONNECT, new Listener {
      override def call(args: AnyRef*): Unit = {
        println("Connected to server")
      }
    })
    .on(Socket.EVENT_DISCONNECT, new Listener {
      override def call(args: AnyRef*): Unit = {
        println("Disconnected from server")
      }
    })
    .on(Socket.EVENT_CONNECT_ERROR, new Listener {
      override def call(args: AnyRef*): Unit = {
        println(s"Can 't connect to $uri")
      }
    })
    .on(Socket.EVENT_MESSAGE, new Listener {
      override def call(args: AnyRef*): Unit = {
        if (args.head != null && args.head.isInstanceOf[String]) {
          onReceive(args.head.asInstanceOf[String])
        } else {
          println("Undefined object received from server")
        }
      }
    })

  def connect(): Unit ={
    socket.connect()
    println("Connection was opened")
  }

  def disconnect(): Unit ={
    socket.disconnect()
    println("Connection was closed")
  }

  override def sendToNetwork(msg: String): Unit = {
    socket.send(msg)
  }
}
