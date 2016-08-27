package com.catinthedark.server.models

import com.corundumstudio.socketio.SocketIOClient

case class Player(
  room: Room,
  socket: SocketIOClient,
  entity: PlayerModel
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
