package com.catinthedark.server

import com.corundumstudio.socketio.SocketIOClient

object IP {
  def retrieve(socket: SocketIOClient): String = try {
    var address = socket.getHandshakeData.getHttpHeaders.get("X-Forwarded-For")
    if (address == null) {
      address = socket.getHandshakeData.getAddress.getHostString
    }
    address
  } catch {
    case e: Exception => ""
  }
}
