package com.catinthedark.ld36.units

import com.catinthedark.common.Const
import com.catinthedark.ld36.Shared0
import com.catinthedark.lib.{MagicSpriteBatch, SimpleUnit}

/**
  * Created by kirill on 28.08.16.
  */
class BrickRenderer(val shared: Shared0) extends SimpleUnit {
  def render(delta: Float, magicBatch: MagicSpriteBatch) = {
    magicBatch.managed { self =>
      shared.bricks.foreach(brick => {
        val texture = brick.texture(delta)
        self.draw(texture,
          brick.pos.x - texture.getRegionWidth / 2 - Const.Projection.width / 2,
          brick.pos.y - texture.getRegionHeight / 2 - Const.Projection.height / 2,
          texture.getRegionWidth / 2, texture.getRegionHeight / 2,
          texture.getRegionWidth, texture.getRegionHeight,
          1, 1,
          brick.angle)
      })
    }
  }
}
