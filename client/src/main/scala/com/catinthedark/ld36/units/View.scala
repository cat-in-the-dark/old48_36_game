package com.catinthedark.ld36.units

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.{Gdx, Input}
import com.catinthedark.ld36.hud.{ShootRageRender, StatsRender, TimerRender}
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
    statsRender.render(shared.stats)
  }

  def controlRotation() = {
    val pointerX = Const.Projection.calcX(Gdx.input.getX()) + camera.position.x
    val pointerY = Const.Projection.height - Const.Projection.calcY(Gdx.input.getY()) + camera.position.y
    val newAngle = new Vector2(pointerX, pointerY).sub(shared.me.pos).angle() - 90
    shared.me.angle = newAngle
  }

  override def run(delta: Float): Unit = {
    controlRotation()

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
