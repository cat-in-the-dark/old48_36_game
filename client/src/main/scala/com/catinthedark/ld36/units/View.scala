package com.catinthedark.ld36.units

import com.badlogic.gdx.{Input, Gdx}
import com.catinthedark.ld36.{Shared0, Assets, Player, Shared1}
import com.catinthedark.ld36.common.{StatsRender, Stat, Stats, Const}
import com.catinthedark.lib.{MagicSpriteBatch, SimpleUnit}

/**
  * Created by kirill on 27.08.16.
  */
class View(val shared: Shared0) extends SimpleUnit {
  val magicBatch = new MagicSpriteBatch(Const.debugEnabled())
  val statsRender = new StatsRender()

  def drawField(): Unit = {
    magicBatch.managed { self =>
      self.draw(Assets.Textures.field, 0, 0, Assets.Textures.field.getWidth, Assets.Textures.field.getHeight)
    }
  }

  def drawStats() = {
    val stats = Stats(me = Stat("over", 1, 1), other = Seq(Stat("ilya", 0, 2), Stat("kirill", 10, 1)))
    statsRender.render(stats)
  }

  override def run(delta: Float): Unit = {
    drawField()
    if (Gdx.input.isKeyPressed(Input.Keys.TAB)) drawStats()
  }
}
