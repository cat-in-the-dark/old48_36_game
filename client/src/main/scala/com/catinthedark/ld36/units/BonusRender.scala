package com.catinthedark.ld36.units

import com.catinthedark.common.Const
import com.catinthedark.ld36.Shared0
import com.catinthedark.lib.MagicSpriteBatch

class BonusRender(val shared: Shared0) {
  def render(delta: Float, batch: MagicSpriteBatch): Unit = {
    shared.bonuses.foreach(b => {
      batch.managed(it => {
        it.drawCentered(b.texture(delta), b.pos.x - Const.Projection.width / 2, b.pos.y - Const.Projection.height / 2)
      })
    })
  }
}
