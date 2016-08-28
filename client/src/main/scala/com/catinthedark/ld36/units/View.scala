package com.catinthedark.ld36.units

import com.badlogic.gdx.graphics.OrthographicCamera
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
  val camera = new OrthographicCamera(Const.Projection.width, Const.Projection.height)

  val magicBatch = new MagicSpriteBatch(Const.debugEnabled)
  val statsRender = new StatsRender()
  val shootRageRender = new ShootRageRender
  val playerRender = new PlayerRender(shared)
  val timeRender = new TimerRender
  val fansRender = new FansRender


  def drawField(): Unit = {
    magicBatch.managed { self =>
      val tex = Assets.Textures.field
      self.draw(tex, -Const.Projection.width / 2, -Const.Projection.height / 2, tex.getWidth, tex.getHeight)
    }
  }

  def drawStats() = {
    val stats = Stats(me = Stat("over", 1, 1), other = Seq(Stat("ilya", 0, 2), Stat("kirill", 10, 1)))
    statsRender.render(stats)
  }

  override def run(delta: Float): Unit = {
    if (shared.me.pos.x > Const.Projection.width / 2
      && shared.me.pos.x < Const.Projection.mapLeftBorder)
      camera.position.x = shared.me.pos.x - Const.Projection.width / 2
    if (shared.me.pos.y > Const.Projection.height / 2
      && shared.me.pos.y < Const.Projection.mapTopBorder)
      camera.position.y = shared.me.pos.y - Const.Projection.height / 2

    camera.update()

    magicBatch.setProjectionMatrix(camera.combined)
    drawField()
    fansRender.render(camera, delta, shared, shared.fansRageMode)
    playerRender.render(delta, magicBatch)
    timeRender.render(shared.timeRemains)
    shootRageRender.render(shared.shootRage)
    if (Gdx.input.isKeyPressed(Input.Keys.TAB)) drawStats()
  }
}
