package com.catinthedark.server.models

import com.badlogic.gdx.math.Vector2
import com.catinthedark.common.Const.Balance
import com.catinthedark.models.PlayerModel
import com.corundumstudio.socketio.SocketIOClient

case class Player(
  room: Room,
  socket: SocketIOClient,
  entity: PlayerModel
) {
  var moveVector = new Vector2(0, 0)

  def pos = new Vector2(entity.x, entity.y)

  def intersect(player: Player): Boolean = {
    pos.dst(player.pos) < (Balance.playerRadius * 2)
  }

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
