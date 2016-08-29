package com.catinthedark.ld36.units

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.catinthedark.common.Const
import com.catinthedark.ld36.{Assets, Shared0}
import com.catinthedark.lib.{MagicSpriteBatch, SimpleUnit}

/**
  * Created by kirill on 27.08.16.
  */
class PlayerRender(val shared: Shared0) extends SimpleUnit {
  private def draw(batch: MagicSpriteBatch, tex: TextureRegion, pos: Vector2, angle: Float): Unit ={
    batch.draw(tex,
      pos.x - tex.getRegionWidth / 2 - Const.Projection.width / 2,
      pos.y - tex.getRegionHeight / 2 - Const.Projection.height / 2,
      tex.getRegionWidth / 2, tex.getRegionHeight / 2,
      tex.getRegionWidth, tex.getRegionHeight,
      1, 1,
      angle)
  }

  def render(delta: Float, magicBatch: MagicSpriteBatch) = {
    magicBatch.managed { self =>
      val me = shared.me
      me.currentPos = me.previousPos.lerp(me.pos, (shared.syncTime / shared.syncDelay).toFloat)
      draw(self, me.texture(delta), me.currentPos, me.angle)
      if (me.hasArmor) {
        draw(self, Assets.Textures.kepaRegion, me.currentPos, me.angle)
      }

      shared.enemies.foreach(enemy => {
        enemy.currentPos = enemy.previousPos.lerp(enemy.pos, (shared.syncTime / shared.syncDelay).toFloat)
        draw(magicBatch, enemy.texture(delta), enemy.currentPos, enemy.angle)
        if (enemy.hasArmor) {
          draw(self, Assets.Textures.kepaRegion, enemy.currentPos, enemy.angle)
        }
      })
    }
  }
}
