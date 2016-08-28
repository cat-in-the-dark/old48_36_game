package com.catinthedark.ld36.hud

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.catinthedark.ld36.Assets
import com.catinthedark.ld36.common.Stats
import com.catinthedark.lib.Magic._

class TimerRender {
  val batch = new SpriteBatch

  def render(remaings: Int) =
    batch.managed { self: SpriteBatch =>
      Assets.Fonts.statsMain2.draw(self, s"${"%02d".format(remaings / 60)}:${"%02d".format(remaings % 60)}", 555, 640)
    }
}
