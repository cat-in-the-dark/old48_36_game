package com.catinthedark.lib

/**
  * Created by over on 13.12.14.
  */
abstract class Stub(name: String) extends YieldUnit[Any, Unit] {

  override def toString = name

  override def onActivate(data: Any) = {}

  override def onExit() = {}

  def run(delta: Float): Option[Unit] = None
}
