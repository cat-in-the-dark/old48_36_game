package com.catinthedark.server.models

import com.corundumstudio.socketio.SocketIOClient

class Player(
  val room: Room,
  val socket: SocketIOClient
) {

  def ip(): String = try {
      var address = socket.getHandshakeData.getHttpHeaders.get("X-Forwarded-For")
      if (address == null) {
        address = socket.getHandshakeData.getAddress.getHostString
      }
      address
    } catch {
      case e: Exception => ""
    }
}
