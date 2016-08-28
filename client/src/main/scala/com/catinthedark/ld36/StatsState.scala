package com.catinthedark.ld36


import com.badlogic.gdx.{Gdx, Input, InputAdapter}
import com.catinthedark.ld36.common.Stats
import com.catinthedark.ld36.hud.StatsRender
import com.catinthedark.lib.YieldUnit
import org.lwjgl.opengl.GL11


class StatsState extends YieldUnit[Stats, Unit] {
  val statsRender = new StatsRender()
  val delay = 10f
  var stateTime: Float = 0
  var stats: Stats = _


  override def onExit() = {
    Gdx.input.setInputProcessor(null)
  }

  override def onActivate(data: Stats): Unit = {
    stats = data
    stateTime = 0
    Gdx.input.setInputProcessor(new InputAdapter{
      override def keyDown(keyCode: Int): Boolean = {
        keyCode match {
          case Input.Keys.ENTER => stateTime = delay
          case _ =>
        }
        true
      }
    })
  }

  override def run(delta: Float): Option[Unit] = {
    Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT)
    if (stats != null) statsRender.render(stats)

    stateTime += delta
    if (stateTime > delay) Some(Unit)
    else None
  }
}
