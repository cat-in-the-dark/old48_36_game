package com.catinthedark.server.models

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

import com.catinthedark.lib.network.JacksonConverterScala
import com.catinthedark.models.{BulletModel, EventNames, GameStateMessage, GameStateModel}
import com.corundumstudio.socketio.SocketIOClient

import collection.JavaConversions._

case class Room(
  name: UUID,
  converter: JacksonConverterScala,
  maxPlayers: Int = 1000
) {
  val players = new ConcurrentHashMap[UUID, Player]()

  def onTick(): Unit = {
    players.iterator.foreach( player => {
      val gameStateModel = GameStateModel(player._2.entity, players.iterator.filter( p => {
        !p._1.equals(player._1)
      }).map( p => {
        p._2.entity
      }).toList, null, null, 0)
      player._2.socket.sendEvent(EventNames.MESSAGE, converter.toJson(GameStateMessage(gameStateModel)))
    })
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
