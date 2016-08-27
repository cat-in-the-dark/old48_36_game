package com.catinthedark.ld36.units

import com.catinthedark.ld36.Shared0
import com.catinthedark.lib.{MagicSpriteBatch, SimpleUnit}

/**
  * Created by kirill on 27.08.16.
  */
abstract class PlayerView(val shared: Shared0) extends SimpleUnit {
  def render(delta: Float, magicBatch: MagicSpriteBatch) = {
  }
}
