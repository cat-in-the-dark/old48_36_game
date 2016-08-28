package com.catinthedark.ld36

import com.catinthedark.ld36.units.{Control, View}
import com.catinthedark.lib.{LocalDeferred, SimpleUnit, YieldUnit}
import com.badlogic.gdx.{Gdx, Input}
import com.catinthedark.ld36.common.{Stat, Stats}
import com.catinthedark.lib.YieldUnit
import com.catinthedark.models.{GameStateModel, MessageConverter}

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
    control.onGameReload + (_ => {
      forceReload = true
      stopNetworkThread()
    })
  }

  def onGameState(gameStateModel: GameStateModel): Unit = {
    shared.me.pos.x = gameStateModel.me.x
    shared.me.pos.y = gameStateModel.me.y
    shared.me.angle = gameStateModel.me.angle
    shared.me.state = MessageConverter.convertStringToState(gameStateModel.me.state)
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
      val stats = Stats(me = Stat("over", 1, 1), other = Seq(Stat("ilya", 0, 2), Stat("kirill", 10, 1)))
      Some(stats)
    }
    else None
  }
}
