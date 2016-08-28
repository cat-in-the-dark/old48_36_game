package com.catinthedark.ld36

import java.util.UUID

import com.badlogic.gdx.math.Vector2
import com.catinthedark.ld36.units.{Control, View}
import com.catinthedark.lib.{LocalDeferred, SimpleUnit, YieldUnit}
import com.badlogic.gdx.{Gdx, Input}
import com.catinthedark.common.Const.Balance
import com.catinthedark.ld36.Assets.Animations.gopAnimationPack
import com.catinthedark.ld36.common.{Stat, Stats}
import com.catinthedark.lib.YieldUnit
import com.catinthedark.models.{GameStateModel, IDLE, MessageConverter}

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

  def onGameState(gameStateModel: GameStateModel): Unit = {
    shared.gameState = gameStateModel
    shared.timeRemains = gameStateModel.time
    val remoteMe = gameStateModel.me
    if (shared.me.id == null) {
      shared.me.id = remoteMe.id
    } else {
      shared.me.pos.x = remoteMe.x
      shared.me.pos.y = remoteMe.y
      shared.me.angle = remoteMe.angle
      shared.me.state = MessageConverter.stringToState(remoteMe.state)
      shared.me.hasBrick = remoteMe.hasBrick
    }

    val enemiesIDs: List[UUID] = shared.enemies.map(enemy => {
      enemy.id
    }).toList

    val remotePlayersIds: List[UUID] = gameStateModel.players.map(p => {
      p.id
    })

    gameStateModel.players.filter(p => {
      enemiesIDs.indexOf(p.id) == -1
    }).foreach(p => {
      shared.enemies.insert(0, PlayerView(new Vector2(p.x, p.y), MessageConverter.stringToState(p.state), p.angle, p.id, p.hasBrick))
    })

    shared.enemies --= shared.enemies.filter(enemy => {
      remotePlayersIds.indexOf(enemy.id) == -1
    })

    shared.enemies.foreach(enemy => {
      val remotePlayer = gameStateModel.players.find(p => {
        p.id.equals(enemy.id)
      }).toList.head
      enemy.pos.x = remotePlayer.x
      enemy.pos.y = remotePlayer.y
      enemy.angle = remotePlayer.angle
      enemy.state = MessageConverter.stringToState(remotePlayer.state)
      enemy.hasBrick = remotePlayer.hasBrick
    })
  }

  def stopNetworkThread(): Unit = {
    println("Trying to stop network thread")
    shared.stopNetwork()
  }

  override def onExit(): Unit = {
    children.foreach(_.onExit())
  }

  override def run(delta: Float): Option[Stats] = {
    shared.networkControl.processIn()
    children.foreach(_.run(delta))
    if (forceReload) {
      forceReload = false
      Some(shared.stats)
    }
    else None
  }
}
