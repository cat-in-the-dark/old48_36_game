package com.catinthedark.lib

/**
  * Created by over on 13.12.14.
  */
trait YieldUnit[-U, +T] {
  def onActivate(data: U)

  def run(delta: Float): Option[T]

  def onExit()
}
