package com.catinthedark.ld36

import com.catinthedark.ld36.units.{Control, View}
import com.catinthedark.lib.{LocalDeferred, SimpleUnit, YieldUnit}
import com.badlogic.gdx.{Gdx, Input}
import com.catinthedark.ld36.common.Stat
import com.catinthedark.lib.YieldUnit

/**
  * Created by over on 18.04.15.
  */

class GameState extends YieldUnit[Shared0, Seq[Stat]] {
  var shared: Shared0 = _
  var shared1: Shared1 = _
  var view: View = _
  var control: Control = _
  var children: Seq[SimpleUnit] = Seq()

  override def onActivate(data: Shared0): Unit = {
    shared = data
    view = new View(shared)
    control = new Control()
    children = Seq(view, control)
    children.foreach(_.onActivate())
  }

  override def onExit(): Unit = {
    children.foreach(_.onExit())
  }

  override def run(delta: Float): Option[Seq[Stat]] = {

    children.foreach(_.run(delta))
    if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) Some(Seq(Stat("over", 1, 1), Stat("ilya", 0, 2), Stat("kirill", 10, 1)))
    else None
  }
}
