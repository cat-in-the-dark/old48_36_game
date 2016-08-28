package com.catinthedark.server.models

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

import com.catinthedark.common.Const
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
    players.iterator.foreach( player => {
      val gameStateModel = buildState(player)
      player._2.socket.sendEvent(EventNames.MESSAGE, converter.toJson(GameStateMessage(gameStateModel)))
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
      PlayerModel(UUID.randomUUID(), playerName, pos.x, pos.y, 0f, MessageConverter.stateToString(IDLE), List(), 0, 0, false))
  }

  def connect(player: Player): Unit = {
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
