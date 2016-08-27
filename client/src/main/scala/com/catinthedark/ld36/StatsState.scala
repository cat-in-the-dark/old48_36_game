package com.catinthedark.ld36


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.catinthedark.ld36.common.Stat
import com.catinthedark.lib.{DelayState, YieldUnit}
import com.catinthedark.lib.Magic._
import org.lwjgl.opengl.GL11


class StatsState extends YieldUnit[Seq[Stat], Unit] {
  val batch = new SpriteBatch
  val delay = 10f
  var stateTime: Float = 0
  var stats: Seq[Stat] = _


  override def onExit() = {}

  override def onActivate(data: Seq[Stat]): Unit = {
    stats = data
    stateTime = 0
  }

  override def run(delta: Float): Option[Unit] = {
    Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT)
    batch.managed { self: SpriteBatch =>
      Assets.Fonts.enterName.draw(self, "Squatality!", 300, 400)
      stats.zipWithIndex.foreach {
        case (index, stat) =>
      }
    }

    stateTime += delta
    if (stateTime > delay) Some(Unit)
    else None
  }
}
