package com.catinthedark.server.models

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

import com.corundumstudio.socketio.SocketIOClient

case class Room(
  name: UUID,
  maxPlayers: Int = 1000
) {
  val players = new ConcurrentHashMap[UUID, Player]()

  def onTick(): Unit = {

  }

  def connect(player: Player): Boolean = {
    if (hasFreePlace()) {
      players.put(player.socket.getSessionId, player) != null
    } else {
      false
    }
  }

  def disconnect(client: SocketIOClient): Boolean = {
    players.remove(client) != null
  }

  def hasFreePlace(): Boolean = players.size() < maxPlayers
}
