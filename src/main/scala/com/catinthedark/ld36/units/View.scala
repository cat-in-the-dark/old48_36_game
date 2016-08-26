package com.catinthedark.ld36.units

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics._
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.catinthedark.ld36.common.Const
import com.catinthedark.ld36.common.Const.UI
import com.catinthedark.lib._

import scala.collection.mutable

/**
  * Created by over on 02.01.15.
  */
abstract class View(val shared: Shared1) extends SimpleUnit with Deferred {
  val batch = new SpriteBatch()
  val magicBatch = new MagicSpriteBatch(Const.debugEnabled())
  val playerBatch = new MagicSpriteBatch(Const.debugEnabled())

  val hudBatch = new ShapeRenderer()

  val shapeRenderer = new ShapeRenderer(5000)

  val enemyView = new EnemyView(shared) with LocalDeferred
  val camera = new OrthographicCamera(Const.Projection.width, Const.Projection.height)

  val renderList = new mutable.ArrayBuffer[() => Unit]()

  override def onActivate() = {
    camera.position.x = Const.Projection.width / 2
    camera.position.y = Const.Projection.height / 2
    camera.update()
    renderList.clear()
  }

  override def run(delta: Float) = {
    //1. clear screen
    Gdx.gl.glClearColor(UI.darknessRed, UI.darknessGreen, UI.darknessBlue, 0)
    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

    camera.update()
    batch.setProjectionMatrix(camera.combined)
    magicBatch.setProjectionMatrix(camera.combined)
    playerBatch.setProjectionMatrix(camera.combined)
    shapeRenderer.setProjectionMatrix(camera.combined)

    enemyView.run(delta)
  }

  override def onExit() = {
  }
}
