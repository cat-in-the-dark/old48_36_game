package com.catinthedark.ld36


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.catinthedark.ld36.common.{StatsRender, Stats, Stat}
import com.catinthedark.lib.{DelayState, YieldUnit}
import com.catinthedark.lib.Magic._
import org.lwjgl.opengl.GL11


class StatsState extends YieldUnit[Stats, Unit] {
  val statsRender = new StatsRender()
  val delay = 10f
  var stateTime: Float = 0
  var stats: Stats = _


  override def onExit() = {}

  override def onActivate(data: Stats): Unit = {
    stats = data
    stateTime = 0
  }

  override def run(delta: Float): Option[Unit] = {
    Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT)
    statsRender.render(stats)

    stateTime += delta
    if (stateTime > delay) Some(Unit)
    else None
  }
}
