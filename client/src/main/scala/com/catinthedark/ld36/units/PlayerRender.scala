package com.catinthedark.ld36.units

import com.catinthedark.ld36.Shared0
import com.catinthedark.lib.{MagicSpriteBatch, SimpleUnit}

/**
  * Created by kirill on 27.08.16.
  */
class PlayerRender(val shared: Shared0) extends SimpleUnit {
  def render(delta: Float, magicBatch: MagicSpriteBatch) = {
    magicBatch.managed { self =>
      val me = shared.me
      val tex = me.texture(delta)
      self.draw(tex,
        me.pos.x - tex.getRegionWidth / 2, me.pos.y - tex.getRegionHeight / 2,
        tex.getRegionWidth / 2, tex.getRegionHeight / 2,
        tex.getRegionWidth, tex.getRegionHeight,
        1, 1,
        me.angle)
      shared.enemies.foreach( enemy => {
        val enemyTex = enemy.texture(delta)
        self.draw(enemyTex,
          enemy.pos.x - enemyTex.getRegionWidth / 2,
          enemy.pos.y - enemyTex.getRegionHeight / 2,
          enemyTex.getRegionWidth / 2, enemyTex.getRegionHeight / 2,
          enemyTex.getRegionWidth, enemyTex.getRegionHeight,
          1, 1,
          enemy.angle)
      })
    }
  }
}
