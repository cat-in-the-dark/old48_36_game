package com.catinthedark.ld36.units

import com.catinthedark.lib.{Deferred, MagicSpriteBatch, SimpleUnit}

abstract class EnemyView(val shared: Shared1) extends SimpleUnit with Deferred {
  def onDisconnect(u: Unit): Unit = {
  }

  def render(delta: Float, magicBatch: MagicSpriteBatch) = {
  }
}
