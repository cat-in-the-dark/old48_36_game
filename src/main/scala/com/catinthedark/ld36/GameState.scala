package com.catinthedark.ld36

import com.catinthedark.ld36.units._
import com.catinthedark.lib.{LocalDeferred, SimpleUnit, YieldUnit}

/**
  * Created by over on 18.04.15.
  */
class GameState(shared0: Shared0) extends YieldUnit[Boolean] {
  var shared1: Shared1 = null
  var view: View = null
  var control: Control = null
  var children: Seq[SimpleUnit] = Seq()

  var forceReload = false

  def activateControl() {
    control.onGameReload + (_ => {
      forceReload = true
      stopNetworkThread()
    })
  }

  def deactivateControl(): Unit = {
    shared0.networkControl.onEnemyDisconnected.ports.clear()
  }

  def stopNetworkThread(): Unit = {
    println("Trying to stop network thread")
    shared0.stopNetwork()
  }

  override def onActivate(data: Any): Unit = {
    Assets.Audios.bgm.play()
    shared1 = data.asInstanceOf[Shared1]
    view = new View(shared1) with LocalDeferred
    control = new Control(shared1) with LocalDeferred
    children = Seq(view, control)
    activateControl()
    children.foreach(_.onActivate())
  }

  override def onExit(): Unit = {
    println("onExit GameState")
    Assets.Audios.bgm.pause()
    children.foreach(_.onExit())
    deactivateControl()
    shared1.reset()
    
    shared1 = null
    children = null
    view = null
    control = null
  }

  override def run(delta: Float): (Option[Boolean], Any) = {
    shared0.networkControl.processIn()
    children.foreach(_.run(delta))
    
    val res = if (forceReload) {
      forceReload = false
      Some(false)
    } else {
      None
    }
    
    (res, null)
  }
}
