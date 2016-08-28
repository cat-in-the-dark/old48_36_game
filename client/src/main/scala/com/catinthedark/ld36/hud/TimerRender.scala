package com.catinthedark.ld36.hud

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.catinthedark.ld36.Assets
import com.catinthedark.ld36.common.Stats
import com.catinthedark.lib.Magic._

class TimerRender {
  val batch = new SpriteBatch

  /**
    * draw timer
    * @param timeRemains is seconds until the END
    */
  def render(timeRemains: Long) =
    batch.managed { self: SpriteBatch =>
      Assets.Fonts.statsMain2.draw(self, s"${"%02d".format(timeRemains / 60)}:${"%02d".format(timeRemains % 60)}", 555, 640)
    }
}
