package com.catinthedark.ld36.units

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.catinthedark.common.Const
import com.catinthedark.ld36.{Assets, Shared0}
import com.catinthedark.lib.{MagicSpriteBatch, MathUtilsLib, SimpleUnit}

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
    var alpha = (shared.syncTime / shared.syncDelay).toFloat
    // just check alpha for bounds
    if (alpha.isNaN) alpha = 1
    if (alpha > 1) alpha = 1
    if (alpha < 0) alpha = 0

    magicBatch.managed { self =>
      val me = shared.me
//      me.currentPos = MathUtilsLib.roundVector(MathUtilsLib.lerp(me.previousPos, me.pos, alpha))
      me.currentPos = me.pos
      draw(self, me.texture(delta), me.currentPos, me.angle)
      if (me.hasArmor) {
        draw(self, Assets.Textures.kepaRegion, me.currentPos, me.angle)
      }

      shared.enemies.foreach(enemy => {
        enemy.currentPos = MathUtilsLib.roundVector(MathUtilsLib.lerp(enemy.previousPos, enemy.pos, alpha))
        draw(self, enemy.texture(delta), enemy.currentPos, enemy.angle)
        if (enemy.hasArmor) {
          draw(self, Assets.Textures.kepaRegion, enemy.currentPos, enemy.angle)
        }
      })
    }
  }
}
