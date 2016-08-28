package com.catinthedark.server.models

import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

import com.badlogic.gdx.math.Vector2
import com.catinthedark.common.Const
import com.catinthedark.common.Const.{Balance, UI}
import com.catinthedark.lib.network.JacksonConverterScala
import com.catinthedark.models._
import com.corundumstudio.socketio.SocketIOClient
import scala.collection.mutable

import collection.JavaConversions._
import scala.collection.mutable.ListBuffer

case class Room(
  name: UUID,
  converter: JacksonConverterScala,
  maxPlayers: Int = 1000
) {
  val players = new ConcurrentHashMap[UUID, Player]()
  val bricks = new ListBuffer[Brick]
  val bonuses = new mutable.ListBuffer[BonusModel]
  var timeRemains = Const.Balance.roundTime

  def checkTimer() = {
    if (players.size() < 1) {
      timeRemains = Const.Balance.roundTime
      bonuses.clear()
      bricks.clear()
    }
  }

  def finishRound(): Unit = {
    println("Round finished")
    timeRemains = Const.Balance.roundTime
    players.iterator.foreach(player => {
      val model = buildGameState(player)
      player._2.socket.sendEvent(EventNames.MESSAGE, converter.toJson(RoundEndsMessage(model)))
    })
    bonuses.clear()
    bricks.clear()
  }

  def intersectWalls(x: Float, y: Float): Boolean = {
    (x < UI.horizontalBorderWidth
      || x > UI.horizontalBorderWidth + UI.fieldWidth
      || y < UI.verticalBorderWidth
      || y > UI.verticalBorderWidth + UI.fieldHeight)
  }

  def onTick(): Unit = {
    players.iterator.foreach( p1 => {
      val intersectedPlayersCount = players.iterator.count(p2 => {
        !p1._1.equals(p2._1) && (new Vector2(p1._2.entity.x, p1._2.entity.y).dst(new Vector2(p2._2.entity.x, p2._2.entity.y)) < Balance.playerRadius * 2)
      })

      if (intersectedPlayersCount > 0 || intersectWalls(p1._2.entity.x, p1._2.entity.y)) {
        p1._2.entity.x = p1._2.entity.oldX
        p1._2.entity.y = p1._2.entity.oldY
      } else {
        p1._2.entity.oldX = p1._2.entity.x
        p1._2.entity.oldY = p1._2.entity.y
      }

      if (!p1._2.entity.hasBrick) {
        val intersectedBricks = bricks.filter(brick => {
          (new Vector2(p1._2.entity.x, p1._2.entity.y).dst(new Vector2(brick.entity.x, brick.entity.y))
            < Balance.playerRadius + Balance.brickRadius)
        })

        if (intersectedBricks.nonEmpty) {
          p1._2.entity.hasBrick = true
          bricks -= intersectedBricks.head
        }

        val gameStateModel = buildGameState(p1)
        p1._2.socket.sendEvent(EventNames.MESSAGE, converter.toJson(GameStateMessage(gameStateModel)))
      }
    })
  }

  def buildGameState(player: (UUID, Player)): GameStateModel ={
    GameStateModel(player._2.entity, players.iterator.filter( p => {
      !p._1.equals(player._1)
    }).map( p => {
      p._2.entity
    }).toList, bricks.map(b => b.entity).toList, bonuses.toList, timeRemains)
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

  def spawnBonus(): Unit = {
    if (bonuses.length < Const.Balance.bonusesAtOnce && players.size() > 1) {
      val pos = Const.Balance.randomSpawn
      val typeName = Const.Balance.randomBonus
      bonuses += BonusModel(UUID.randomUUID(), pos.x, pos.y, typeName)
      println(s"Bonuses: $bonuses")
    }
  }

  def spawnBrick() = {
    println("spawn brick")
    val pos = Balance.randomBrickSpawn
    Brick(0, BrickModel(UUID.randomUUID(), pos.x, pos.y, 0.0f, hurting = false))
  }

  def connect(player: Player): Unit = {
    players.values().iterator().foreach{ player =>
      player.socket.sendEvent(EventNames.MESSAGE, converter.toJson(SoundMessage(SoundNames.Siklo.toString)))
    }
    if (hasFreePlace()) {
      players.put(player.socket.getSessionId, player)
      checkTimer()
      if (players.size() <= 1) {
        bricks.insert(0, spawnBrick())
      }
    }
  }

  def disconnect(client: SocketIOClient): Unit = {
    players.remove(client.getSessionId)
    checkTimer()
  }

  def hasFreePlace(): Boolean = players.size() < maxPlayers
}
