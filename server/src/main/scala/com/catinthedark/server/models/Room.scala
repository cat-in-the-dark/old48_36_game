package com.catinthedark.server.models

import java.util.UUID
import java.util.concurrent.{ConcurrentHashMap, TimeUnit}

import com.badlogic.gdx.math.Vector2
import com.catinthedark.common.Const
import com.catinthedark.common.Const.{Balance, UI}
import com.catinthedark.lib.Intervals
import com.catinthedark.lib.network.JacksonConverterScala
import com.catinthedark.models._
import com.corundumstudio.socketio.SocketIOClient

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

case class Room(
                 name: UUID,
                 converter: JacksonConverterScala,
                 maxPlayers: Int = 1000
               ) {
  val players = new ConcurrentHashMap[UUID, Player]()
  val bricks = new ListBuffer[Brick]
  val bonuses = new mutable.ListBuffer[BonusModel]
  var timeRemains = Const.Balance.roundTime
  val executor = new Intervals(1)

  def checkTimer(): Unit = {
    if (players.size() < 1) {
      timeRemains = Const.Balance.roundTime
      bonuses.clear()
      bricks.clear()
    }
  }

  def timerTick(): Unit = {
    checkTimer()
    if (timeRemains > 0) {
      timeRemains -= 1
    } else {
      finishRound()
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
    bricks.foreach(brick => {
      if (intersectWalls(brick.entity.x, brick.entity.y)) {
        brick.entity.hurting = false
        brick.initialSpeed = 0
        brick.currentSpeed = 0
      } else {
        brick.entity.x -= brick.currentSpeed * Math.sin(Math.toRadians(brick.entity.angle)).toFloat
        brick.entity.y += brick.currentSpeed * Math.cos(Math.toRadians(brick.entity.angle)).toFloat
      }
    })

    players.iterator.foreach(p1 => {
      if (!p1._2.entity.state.equals(MessageConverter.stateToString(KILLED))) {

        val intersectedPlayersCount = players.iterator.count(p2 => {
          (!p1._1.equals(p2._1)
            && (new Vector2(p1._2.entity.x, p1._2.entity.y).dst(new Vector2(p2._2.entity.x, p2._2.entity.y)) < Balance.playerRadius * 2)
            && !p2._2.entity.state.equals(MessageConverter.stateToString(KILLED)))
        })

        if (intersectedPlayersCount > 0 || intersectWalls(p1._2.entity.x, p1._2.entity.y)) {
          p1._2.entity.x = p1._2.entity.oldX
          p1._2.entity.y = p1._2.entity.oldY
        } else {
          p1._2.entity.oldX = p1._2.entity.x
          p1._2.entity.oldY = p1._2.entity.y
        }

        val intersectedBricks = bricks.filter(brick => {
          (new Vector2(p1._2.entity.x, p1._2.entity.y).dst(new Vector2(brick.entity.x, brick.entity.y))
            < Balance.playerRadius + Balance.brickRadius)
        })

        if (intersectedBricks.nonEmpty) {
          val killerBricks = intersectedBricks.filter(brick => {
            brick.entity.hurting
          })
          if (killerBricks.nonEmpty) {
            if (p1._2.entity.bonuses.nonEmpty) {
              p1._2.entity.bonuses.clear()
              killerBricks.foreach(brick => {
                brick.entity.angle += 180.0f
              })
            } else {
              p1._2.entity.state = MessageConverter.stateToString(KILLED)
              p1._2.entity.deaths += 1
              players.values().foreach { player =>
                player.socket.sendEvent(EventNames.MESSAGE, converter.toJson(SoundMessage(
                  new Random().nextInt(10) match {
                    case 1 => SoundNames.ChponkSuka.toString
                    case 2 => SoundNames.Tooth.toString
                    case _ => SoundNames.HeadShot.toString
                  }
                )))
              }
              killerBricks.foreach(brick => {
                if (brick.throwerID != null) {
                  val player = players.get(brick.throwerID)
                  if (player != null) {
                    player.entity.frags += 1
                  }
                }
              })
              executor.deffer(2, TimeUnit.SECONDS, () => {
                p1._2.entity.state = MessageConverter.stateToString(IDLE)
              })
            }
          } else if (!p1._2.entity.hasBrick) {
            p1._2.entity.hasBrick = true
            bricks -= intersectedBricks.head
          }
        }
      }

      val gameStateModel = buildGameState(p1)
      p1._2.socket.sendEvent(EventNames.MESSAGE, converter.toJson(GameStateMessage(gameStateModel)))
    })

    players.values().foreach { player =>
      val hats = bonuses.filter(_.typeName == Const.Bonus.hat).toList
      hats.foreach { hat =>
        if (new Vector2(hat.x, hat.y).dst(new Vector2(player.entity.x, player.entity.y)) < Const.Balance.playerRadius + Const.Balance.hatRadius) {
          player.entity.bonuses += hat.typeName
          bonuses -= hat
        }
      }
    }
  }

  def buildGameState(player: (UUID, Player)): GameStateModel = {
    GameStateModel(player._2.entity, players.iterator.filter(p => {
      !p._1.equals(player._1)
    }).map(p => {
      p._2.entity
    }).toList, bricks.map(b => b.entity).toList, bonuses.toList, timeRemains)
  }

  def onMove(client: SocketIOClient, msg: MoveMessage): Unit = {
    val player: Player = players.get(client.getSessionId)
    if (!player.entity.state.equals(MessageConverter.stateToString(KILLED))) {
      player.entity.x += msg.speedX
      player.entity.y += msg.speedY
      player.entity.angle = msg.angle
      player.entity.state = msg.stateName
    }
  }

  def onThrow(client: SocketIOClient, msg: ThrowBrickMessage): Unit = {
    bricks.insert(0, Brick(msg.force, msg.force, BrickModel(UUID.randomUUID(), msg.x, msg.y, msg.angle, hurting = true), client.getSessionId))
    players.get(client.getSessionId).entity.hasBrick = false
  }

  def spawnPlayer(client: SocketIOClient, playerName: String): Boolean = {
    val pos = Const.Balance.randomSpawn
    val player = Player(this, client,
      PlayerModel(UUID.randomUUID(), playerName, pos.x, pos.y, pos.x, pos.y, 0f, MessageConverter.stateToString(IDLE), mutable.ListBuffer(), 0, 0, false))
    connect(player)
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
    Brick(0, 0, BrickModel(UUID.randomUUID(), pos.x, pos.y, 0.0f, hurting = false))
  }

  def connect(player: Player): Boolean = {
    if (hasFreePlace()) {
      players.values().iterator().foreach { player =>
        player.socket.sendEvent(EventNames.MESSAGE, converter.toJson(SoundMessage(SoundNames.Siklo.toString)))
      }
      players.put(player.socket.getSessionId, player)
      checkTimer()
      if (players.size() <= 1) {
        bricks.insert(0, spawnBrick())
        bricks.insert(0, spawnBrick())
      }
      true
    } else {
      false
    }
  }

  def disconnect(client: SocketIOClient): Unit = {
    val playerToRemove = players.get(client.getSessionId)
    if (playerToRemove.entity.hasBrick) {
      bricks += Brick(0f, 0f, BrickModel(UUID.randomUUID(), playerToRemove.entity.x, playerToRemove.entity.y, 0f, hurting = false))
    }
    players.remove(client.getSessionId)
    checkTimer()
  }

  def hasFreePlace(): Boolean = players.size() < maxPlayers
}
