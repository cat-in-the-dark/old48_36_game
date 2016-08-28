package com.catinthedark.ld36.units

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Affine2
import com.catinthedark.common.Const
import com.catinthedark.ld36.{Assets, Shared0}
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
        me.pos.x - tex.getRegionWidth / 2 - Const.Projection.width / 2,
        me.pos.y - tex.getRegionHeight / 2 - Const.Projection.height / 2,
        tex.getRegionWidth / 2, tex.getRegionHeight / 2,
        tex.getRegionWidth, tex.getRegionHeight,
        1, 1,
        me.angle)
      if (me.hasArmor) {
        val tex = new TextureRegion(Assets.Textures.kepa)
        self.draw(tex,
          me.pos.x - tex.getRegionWidth / 2 - Const.Projection.width / 2,
          me.pos.y - tex.getRegionHeight / 2 - Const.Projection.height / 2,
          tex.getRegionWidth / 2, tex.getRegionHeight / 2,
          tex.getRegionWidth, tex.getRegionHeight,
          1, 1,
          me.angle)
      }

      shared.enemies.foreach(enemy => {
        val enemyTex = enemy.texture(delta)
        self.draw(enemyTex,
          enemy.pos.x - enemyTex.getRegionWidth / 2 - Const.Projection.width / 2,
          enemy.pos.y - enemyTex.getRegionHeight / 2 - Const.Projection.height / 2,
          enemyTex.getRegionWidth / 2, enemyTex.getRegionHeight / 2,
          enemyTex.getRegionWidth, enemyTex.getRegionHeight,
          1, 1,
          enemy.angle)

        if (enemy.hasArmor) {
          println("here")
          val tex = new TextureRegion(Assets.Textures.kepa)
          self.draw(tex,
            enemy.pos.x - tex.getRegionWidth / 2 - Const.Projection.width / 2,
            enemy.pos.y - tex.getRegionHeight / 2 - Const.Projection.height / 2,
            tex.getRegionWidth / 2, tex.getRegionHeight / 2,
            tex.getRegionWidth, tex.getRegionHeight,
            1, 1,
            enemy.angle)
        }
      })

    }
  }
}
