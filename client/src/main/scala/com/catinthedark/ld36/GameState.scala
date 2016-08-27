package com.catinthedark.ld36

import com.catinthedark.ld36.units.{Control, View}
import com.catinthedark.lib.{LocalDeferred, SimpleUnit, YieldUnit}

/**
  * Created by over on 18.04.15.
  */
class GameState(shared0: Shared0) extends YieldUnit[Boolean] {
  var shared1: Shared1 = _
  var view: View = _
  var control: Control = _
  var children: Seq[SimpleUnit] = Seq()

  override def onActivate(data: Any): Unit = {
    shared1 = data.asInstanceOf[Shared1]
    view = new View(shared1)
    control = new Control()
    children = Seq(view, control)
    children.foreach(_.onActivate())
  }

  override def onExit(): Unit = {
    children.foreach(_.onExit())
  }

  override def run(delta: Float): (Option[Boolean], Any) = {
    children.foreach(_.run(delta))
    (None, null)
  }
}
