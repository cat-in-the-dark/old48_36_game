package com.catinthedark.ld36

import java.util.UUID

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.TimeUtils
import com.catinthedark.common.Const
import com.catinthedark.ld36.common.Stats
import com.catinthedark.ld36.units.{Control, View}
import com.catinthedark.lib.{LocalDeferred, SimpleUnit, YieldUnit}
import com.catinthedark.models.{GameStateModel, MessageConverter, RUNNING}
import com.badlogic.gdx.{Gdx, Input}
import com.catinthedark.common.Const.Balance
import com.catinthedark.ld36.Assets.Animations.gopAnimationPack
import com.catinthedark.ld36.common.{Stat, Stats}
import com.catinthedark.lib
import com.catinthedark.lib.YieldUnit
import com.catinthedark.models._

import collection.JavaConversions._
import scala.collection.mutable.ListBuffer

/**
  * Created by over on 18.04.15.
  */

class GameState extends YieldUnit[Shared0, Stats] {
  var shared: Shared0 = _
  var view: View = _
  var control: Control = _
  var children: Seq[SimpleUnit] = Seq()
  var forceReload = false

  override def onActivate(data: Shared0): Unit = {
    shared = data
    view = new View(shared)
    control = new Control(shared) with LocalDeferred
    children = Seq(view, control)
    children.foreach(_.onActivate())
    activateControl()
    Assets.Audios.bgm.play()
  }

  def activateControl(): Unit = {
    shared.networkControl.onGameStatePipe.ports += onGameState
    shared.networkControl.onRoundEndsPipe.ports += onRoundEnds
    control.onGameReload + (_ => {
      forceReload = true
      stopNetworkThread()
    })
  }

  def onRoundEnds(gameStateModel: GameStateModel): Unit = {
    println("Will go to stats!!")
    onGameState(gameStateModel) // update the last tick
    forceReload = true
    stopNetworkThread()
  }

  def updateEnemies(players: List[PlayerModel]): Unit = {
    val enemiesIDs: List[UUID] = shared.enemies.map(enemy => {
      enemy.id
    }).toList

    val remotePlayersIds: List[UUID] = players.map(p => {
      p.id
    })

    players.filter(p => {
      enemiesIDs.indexOf(p.id) == -1
    }).foreach(p => {
      shared.enemies.insert(0, PlayerView(new Vector2(p.x, p.y), MessageConverter.stringToState(p.state), p.angle, p.id, p.hasBrick, hasArmor = false))
    })

    shared.enemies --= shared.enemies.filter(enemy => {
      remotePlayersIds.indexOf(enemy.id) == -1
    })

    shared.enemies.foreach(enemy => {
      val remotePlayer = players.find(p => {
        p.id.equals(enemy.id)
      }).toList.head
      enemy.pos.x = remotePlayer.x
      enemy.pos.y = remotePlayer.y
      enemy.angle = remotePlayer.angle
      enemy.state = MessageConverter.stringToState(remotePlayer.state)
      enemy.hasArmor = remotePlayer.bonuses.contains(Const.Bonus.hat)
      enemy.hasBrick = remotePlayer.hasBrick
    })
  }

  def updateBricks(bricks: List[BrickModel]): Unit = {
    val brickIDs: List[UUID] = shared.bricks.map(brick => {
      brick.id
    }).toList

    val remoteBrickIds: List[UUID] = bricks.map(brick => {
      brick.id
    })

    bricks.filter(brick => {
      brickIDs.indexOf(brick.id) == -1
    }).foreach(brick => {
      shared.bricks.insert(0, Brick(new Vector2(brick.x, brick.y), brick.angle, brick.id, Balance.brickRadius))
    })

    shared.bricks --= shared.bricks.filter(brick => {
      remoteBrickIds.indexOf(brick.id) == -1
    })

    shared.bricks.foreach(brick => {
      val remoteBrick = bricks.find(b => {
        b.id.equals(brick.id)
      }).toList.head
      brick.pos.x = remoteBrick.x
      brick.pos.y = remoteBrick.y
      brick.angle = remoteBrick.angle
    })
  }

  def syncTime(): Unit = {
    shared.syncTime = 0
    val time = System.nanoTime()
    shared.syncDelay = (time - shared.lastSyncTime) / 1000000000.0f
    shared.lastSyncTime = time
  }

  def onGameState(gameStateModel: GameStateModel): Unit = {
    syncTime()
    shared.gameState = gameStateModel
    shared.timeRemains = gameStateModel.time

    val remoteMe = gameStateModel.me

    shared.me.hasArmor = remoteMe.bonuses.contains(Const.Bonus.hat)

    if (shared.me.id == null) {
      shared.me.id = remoteMe.id
    } else {
      shared.me.pos.x = remoteMe.x
      shared.me.pos.y = remoteMe.y
      shared.me.angle = remoteMe.angle
      shared.me.state = MessageConverter.stringToState(remoteMe.state)
      shared.me.hasBrick = remoteMe.hasBrick
    }

    updateEnemies(gameStateModel.players)
    updateBricks(gameStateModel.bricks)

    val remoteBonusesIDs: List[UUID] = gameStateModel.bonuses.map(_.id)
    val localBonusesIDs: List[UUID] = shared.bonuses.map(_.id).toList

    gameStateModel.bonuses.filter(el => {
      localBonusesIDs.indexOf(el.id) == -1
    }).foreach(el => {
      el.typeName match {
        case Const.Bonus.hat =>
          shared.bonuses.insert(0, HatBonus(el.id, new Vector2(el.x, el.y)))
        case _ => println(s"Undefined Bonus. Yo! $el")
      }
    })

    shared.bonuses --= shared.bonuses.filter(el => {
      remoteBonusesIDs.indexOf(el.id) == -1
    })
  }

  def stopNetworkThread(): Unit = {
    println("Trying to stop network thread")
    shared.stopNetwork()
  }

  override def onExit(): Unit = {
    children.foreach(_.onExit())
  }

  var isBgmStepsPlaying = false

  override def run(delta: Float): Option[Stats] = {
    val isAnybodyRunning = (shared.me.state == RUNNING || shared.enemies.contains { enemy: PlayerView => enemy.state == RUNNING })
    if (isAnybodyRunning) {
      if (!isBgmStepsPlaying)
        Assets.Audios.bgmSteps.play()
      isBgmStepsPlaying = true
    } else {
      if (isBgmStepsPlaying)
        Assets.Audios.bgmSteps.pause()
      isBgmStepsPlaying = false
    }

    shared.networkControl.processIn()
    shared.networkControl.tick()
    children.foreach(_.run(delta))
    if (forceReload) {
      forceReload = false
      Assets.Audios.bgmSteps.stop()
      Assets.Audios.bgm.stop()
      Some(shared.stats)
    }
    else None
  }
}
