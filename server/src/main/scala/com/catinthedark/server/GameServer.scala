package com.catinthedark.server

object GameServer {
  def main(args: Array[String]): Unit = {
    val service = new SocketIOService()
    service.start()
  }
}
