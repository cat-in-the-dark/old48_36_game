package com.catinthedark.ld36.units

import com.catinthedark.ld36.Shared0
import com.catinthedark.lib.{LocalDeferred, MagicSpriteBatch, SimpleUnit}

/**
  * Created by kirill on 27.08.16.
  */
class PlayerRender(val shared: Shared0) extends SimpleUnit {
  def render(delta: Float, magicBatch: MagicSpriteBatch) = {
    magicBatch.managed { self =>
      self.draw(shared.me.texture(delta), shared.me.pos.x, shared.me.pos.y)
    }
  }
}
