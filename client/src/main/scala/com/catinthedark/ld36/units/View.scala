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
  val bonusRender = new BonusRender(shared)


  def drawField(): Unit = {
    magicBatch.managed { self =>
      val tex = Assets.Textures.field
      self.draw(tex, -Const.Projection.width / 2 - 155, -Const.Projection.height / 2 - 200, tex.getWidth, tex.getHeight)
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
    camera.position.x  = Math.max(-130, Math.min(220, shared.me.pos.x - Const.Projection.width / 2));
    camera.position.y  = Math.max(-160, Math.min(220, shared.me.pos.y - Const.Projection.height / 2));
//    camera.position.x = Math.min(shared.me.pos.x - Const.Projection.width / 2, 700)
//    camera.position.y = Math.min(shared.me.pos.y - Const.Projection.height / 2, 500)

    camera.update()

    magicBatch.setProjectionMatrix(camera.combined)
    drawField()
    fansRender.render(camera, delta, shared, shared.fansRageMode)
    bonusRender.render(delta, magicBatch)
    playerRender.render(delta, magicBatch)
    timeRender.render(shared.timeRemains)
    shootRageRender.render(shared.shootRage)
    if (Gdx.input.isKeyPressed(Input.Keys.TAB)) drawStats()
  }
}
