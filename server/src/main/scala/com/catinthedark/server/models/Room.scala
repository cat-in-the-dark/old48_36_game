package com.catinthedark.server.models

import java.util.UUID
import java.util.concurrent.{ConcurrentHashMap, TimeUnit}

import com.badlogic.gdx.math.Vector2
import com.catinthedark.common.Const
import com.catinthedark.common.Const.{Balance, UI}
import com.catinthedark.lib.Intervals
import com.catinthedark.lib.network.JacksonConverterScala
import com.catinthedark.models._
import com.catinthedark.server.persist.IRepository
import com.corundumstudio.socketio.SocketIOClient

import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.util.Random

case class Room(
                 name: UUID,
                 converter: JacksonConverterScala,
                 repository: IRepository,
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
    repository.onRoundFinish(players.values().toList)
    println("Round finished")
    timeRemains = Const.Balance.roundTime
    players.iterator.foreach(player => {
      val model = buildGameState(player)
      player._2.socket.sendEvent(EventNames.MESSAGE, converter.toJson(RoundEndsMessage(model)))
    })
    bonuses.clear()
    bricks.clear()
  }

  def topWallpenetration(y: Float, radius: Float): Float = {
    Math.max(0f, UI.verticalBorderWidth - (y - radius))
  }

  def bottomWallPenetration(y: Float, radius: Float): Float = {
    Math.max(0f, y + radius - (UI.verticalBorderWidth + UI.fieldHeight))
  }

  def leftWallPenetration(x: Float, radius: Float): Float = {
    Math.max(0f, UI.horizontalBorderWidth - (x - radius))
  }

  def rightWallPenetration(x: Float, radius: Float): Float = {
    Math.max(0f, x + radius - (UI.horizontalBorderWidth + UI.fieldWidth))
  }

  def intersectWalls(x: Float, y: Float, radius: Float): Boolean = {
    (topWallpenetration(y, radius) > 0
      || bottomWallPenetration(y, radius) > 0
      || leftWallPenetration(x, radius) > 0
      || rightWallPenetration(x, radius) > 0)
  }

  def onTick(): Unit = {
    bricks.foreach(brick => {
      if (leftWallPenetration(brick.entity.x, Balance.brickRadius) > 0
        || rightWallPenetration(brick.entity.x, Balance.brickRadius) > 0) {
        brick.entity.angle = new Vector2(Math.cos(Math.toRadians(brick.entity.angle)).toFloat, -1 * Math.sin(Math.toRadians(brick.entity.angle)).toFloat).angle()
      }
      if (topWallpenetration(brick.entity.y, Balance.brickRadius) > 0
        || bottomWallPenetration(brick.entity.y, Balance.brickRadius) > 0) {
        brick.entity.angle = new Vector2(-1 * Math.cos(Math.toRadians(brick.entity.angle)).toFloat, Math.sin(Math.toRadians(brick.entity.angle)).toFloat).angle()
      }

      brick.entity.x -= brick.currentSpeed * Math.sin(Math.toRadians(brick.entity.angle)).toFloat
      brick.entity.y += brick.currentSpeed * Math.cos(Math.toRadians(brick.entity.angle)).toFloat

      if (brick.currentSpeed <= 0) {
        brick.currentSpeed = 0
        brick.entity.hurting = false
      } else {
        brick.currentSpeed -= Balance.brickFriction
      }
    })

    players.iterator.foreach(p1 => {
      if (!p1._2.entity.state.equals(MessageConverter.stateToString(KILLED))) {
        players.iterator.filter(p2 => {
          (!p1._1.equals(p2._1)
            && p1._2.intersect(p2._2)
            && !p2._2.entity.state.equals(MessageConverter.stateToString(KILLED)))
        }).foreach( p2 => {
          p1._2.moveVector.add(p1._2.pos.sub(p2._2.pos).setLength(Balance.playerRadius - p1._2.pos.dst(p2._2.pos) / 2))
        })

        val wallShiftVector = new Vector2()
          .add(leftWallPenetration(p1._2.entity.x + p1._2.moveVector.x, Balance.playerRadius),
            topWallpenetration(p1._2.entity.y + p1._2.moveVector.y, Balance.playerRadius))
          .sub(rightWallPenetration(p1._2.entity.x + p1._2.moveVector.x, Balance.playerRadius),
            bottomWallPenetration(p1._2.entity.y + p1._2.moveVector.y, Balance.playerRadius))

        p1._2.moveVector.add(wallShiftVector)

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

      players.iterator.foreach( p => {
        p._2.entity.x += p._2.moveVector.x
        p._2.entity.y += p._2.moveVector.y
        p._2.moveVector.setZero()
      })

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
      val thrower = players.get(client.getSessionId)
      if (thrower.entity.hasBrick) {
        bricks.insert(0, Brick(msg.force, msg.force, BrickModel(UUID.randomUUID(), msg.x, msg.y, msg.angle, hurting = true), client.getSessionId))
        thrower.entity.hasBrick = false
      }
  }

  def spawnPlayer(client: SocketIOClient, playerName: String): Boolean = {
    val pos = Const.Balance.randomSpawn
    val player = Player(this, client,
      PlayerModel(UUID.randomUUID(), playerName, pos.x, pos.y, 0f, MessageConverter.stateToString(IDLE), mutable.ListBuffer(), 0, 0, false))
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
      repository.onPlayerConnect(player)
      true
    } else {
      false
    }
  }

  def disconnect(client: SocketIOClient): Unit = {
    val playerToRemove = players.get(client.getSessionId)
    repository.onPlayerDisconnect(playerToRemove)
    if (playerToRemove.entity.hasBrick) {
      bricks += Brick(0f, 0f, BrickModel(UUID.randomUUID(), playerToRemove.entity.x, playerToRemove.entity.y, 0f, hurting = false))
    }
    players.remove(client.getSessionId)
    checkTimer()
  }

  def hasFreePlace(): Boolean = players.size() < maxPlayers
}
