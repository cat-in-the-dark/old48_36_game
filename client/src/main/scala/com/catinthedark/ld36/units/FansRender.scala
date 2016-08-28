package com.catinthedark.ld36.units


import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.catinthedark.common.Const
import com.catinthedark.ld36.Shared0
import com.catinthedark.lib.Magic._

class FansRender {
  val batch = new SpriteBatch

  def render(camera: Camera, delta: Float, shared0: Shared0, rageMode: Boolean = false) = {
    batch.setProjectionMatrix(camera.combined)
    batch.managed { self =>
      shared0.fans.foreach { fan =>
        fan.delta += delta * fan.speed * (if (rageMode) 3.0f else 1.0f)
        val tex = fan.animation.normalAnimation.getKeyFrame(fan.delta)
        self.draw(tex, fan.pos.x - tex.getRegionWidth / 2 - Const.Projection.width / 2, fan.pos.y - tex.getRegionHeight / 2 - Const.Projection.height / 2, tex.getRegionWidth / 2, tex.getRegionHeight / 2, tex.getRegionWidth, tex.getRegionHeight, 1, 1, fan.angle)
      }
    }
  }

}
