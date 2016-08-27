package com.catinthedark.ld36.common

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.catinthedark.ld36.Assets
import com.catinthedark.lib.Magic._

class StatsRender {
  val batch = new SpriteBatch
  val shapeRenderer = new ShapeRenderer
  shapeRenderer.setColor(1, 1, 1, 0.5f)


  def render(stats: Stats) = {
    val seq = (stats.other :+ stats.me).sortBy(_.scores).reverse.zipWithIndex

    seq.foreach {
      case (stat, index) =>
        if (stat eq stats.me) {
          shapeRenderer.begin(ShapeType.Filled)
          shapeRenderer.rect(100, 550 - index * 30 - 22, 1000, 30)
          shapeRenderer.end()
        }
    }

    batch.managed { self: SpriteBatch =>
      Assets.Fonts.statsMain.draw(self, "Squatality!", 100, 600)
      Assets.Fonts.statsMain2.draw(self, "______________________________________________________________________________| scores | dead |_______", 100, 580)

      seq.foreach {
        case (stat, index) =>
          val uname = stat.username
          Assets.Fonts.statsEntry.draw(self, uname, 100, 550 - index * 30)
          Assets.Fonts.statsEntry.draw(self, stat.scores.toString, 910, 550 - index * 30)
          Assets.Fonts.statsEntry.draw(self, stat.dead.toString, 990, 550 - index * 30)
      }
    }
  }
}
