package com.catinthedark.server.models

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

import com.badlogic.gdx.math.Vector2
import com.catinthedark.common.Const
import com.catinthedark.common.Const.Balance
import com.catinthedark.lib.network.JacksonConverterScala
import com.catinthedark.models._
import com.corundumstudio.socketio.SocketIOClient

import collection.JavaConversions._

case class Room(
  name: UUID,
  converter: JacksonConverterScala,
  maxPlayers: Int = 1000
) {
  def checkTimer() = {
    if (players.size() < 1) {
      timeRemains = Const.Balance.roundTime
    }
  }

  def finishRound(): Unit = {
    println("Round finished")
    timeRemains = Const.Balance.roundTime
    players.iterator.foreach(player => {
      val model = buildState(player)
      player._2.socket.sendEvent(EventNames.MESSAGE, converter.toJson(RoundEndsMessage(model)))
    })
  }

  val players = new ConcurrentHashMap[UUID, Player]()
  var timeRemains = Const.Balance.roundTime

  def onTick(): Unit = {
    players.iterator.foreach( p1 => {
      val gameStateModel = buildState(p1)
      val intersectedPlayersCount = players.iterator.count(p2 => {
        !p1._1.equals(p2._1) && (new Vector2(p1._2.entity.x, p1._2.entity.y).dst(new Vector2(p2._2.entity.x, p2._2.entity.y)) < Balance.playerRadius)
      })

      if (intersectedPlayersCount > 0) {
        p1._2.entity.x = p1._2.entity.oldX
        p1._2.entity.y = p1._2.entity.oldY
      } else {
        p1._2.entity.oldX = p1._2.entity.x
        p1._2.entity.oldY = p1._2.entity.y
      }

      p1._2.socket.sendEvent(EventNames.MESSAGE, converter.toJson(GameStateMessage(gameStateModel)))
    })
  }

  def buildState(player: (UUID, Player)): GameStateModel ={
    GameStateModel(player._2.entity, players.iterator.filter( p => {
      !p._1.equals(player._1)
    }).map( p => {
      p._2.entity
    }).toList, List(), List(), timeRemains)
  }

  def onMove(client: SocketIOClient, msg: MoveMessage): Unit = {
    val player: Player = players.get(client.getSessionId)
    player.entity.x += msg.speedX
    player.entity.y += msg.speedY
    player.entity.angle = msg.angle
    player.entity.state = msg.stateName
  }

  def spawnPlayer(client: SocketIOClient, playerName: String): Player = {
    val pos = Const.Balance.randomSpawn
    Player(this, client,
      PlayerModel(UUID.randomUUID(), playerName, pos.x, pos.y, pos.x, pos.y, 0f, MessageConverter.stateToString(IDLE), List(), 0, 0, false))
  }

  def connect(player: Player): Unit = {
    players.values().iterator().foreach{ player =>
      player.socket.sendEvent(EventNames.MESSAGE, converter.toJson(SoundMessage(SoundNames.Siklo.toString)))
    }
    if (hasFreePlace()) {
      players.put(player.socket.getSessionId, player)
      checkTimer()
    }
  }

  def disconnect(client: SocketIOClient): Unit = {
    players.remove(client.getSessionId)
    checkTimer()
  }

  def hasFreePlace(): Boolean = players.size() < maxPlayers
}
