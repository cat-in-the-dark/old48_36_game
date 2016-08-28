package com.catinthedark.ld36.units

import com.badlogic.gdx.{Gdx, Input}
import com.catinthedark.ld36.hud.{TimerRender, ShootRageRender, StatsRender}
import com.catinthedark.ld36.{Assets, Shared0}
import com.badlogic.gdx.{Gdx, Input}
import com.catinthedark.common.Const
import com.catinthedark.ld36.common.{Stat, Stats}
import com.catinthedark.ld36.{Assets, Shared0}
import com.catinthedark.lib.{MagicSpriteBatch, SimpleUnit}

/**
  * Created by kirill on 27.08.16.
  */
class View(val shared: Shared0) extends SimpleUnit {
  val magicBatch = new MagicSpriteBatch(Const.debugEnabled)
  val statsRender = new StatsRender()
  val shootRageRender = new ShootRageRender
  val playerRender = new PlayerRender(shared)
  val timeRender = new TimerRender

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
    playerRender.render(delta, magicBatch)
    timeRender.render(73)
    shootRageRender.render(shared.shootRage)
    if (Gdx.input.isKeyPressed(Input.Keys.TAB)) drawStats()
  }
}
