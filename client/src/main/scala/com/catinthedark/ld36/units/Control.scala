package com.catinthedark.ld36.units

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.{Gdx, Input, InputAdapter}
import com.catinthedark.common.Const
import com.catinthedark.common.Const.Balance
import com.catinthedark.ld36.Shared0
import com.catinthedark.lib.{Pipe, SimpleUnit}
import com.catinthedark.models.{IDLE, RUNNING, THROWING}

/**
  * Created by kirill on 27.08.16.
  */
class Control(shared: Shared0) extends SimpleUnit {
  val onGameReload = new Pipe[Unit]()

  override def onActivate(): Unit = {
    Gdx.input.setInputProcessor(new InputAdapter {
      override def keyDown(keycode: Int): Boolean = {
        keycode match {
          case Input.Keys.ESCAPE => onGameReload()
          case _ =>
        }
        true
      }
    })
  }

  def controlShoot(delta: Float) = {
    if (shared.me.hasBrick && shared.me.state == IDLE)
      if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        shared.shootRage = Math.min(shared.shootRage + Balance.shootRageSpeed * delta, Balance.maxShootRage)
      else {
        if (shared.shootRage != 0) {
          shared.me.animationCounter = 0
          shared.me.state = THROWING
        }
        shared.shootRage = 0
      }
    else
      shared.shootRage = 0
  }

  private def controlKeysPressed(): Boolean = {
    Gdx.input.isKeyPressed(Input.Keys.A) ||
      Gdx.input.isKeyPressed(Input.Keys.D) ||
      Gdx.input.isKeyPressed(Input.Keys.W) ||
      Gdx.input.isKeyPressed(Input.Keys.S)
  }

  override def run(delta: Float) = {
    controlShoot(delta)

    val speed = new Vector2(0, 0)
    val playerSpeed = Const.Balance.playerSpeed

    if (Gdx.input.isKeyPressed(Input.Keys.A)) {
      speed.x -= playerSpeed
    }

    if (Gdx.input.isKeyPressed(Input.Keys.D)) {
      speed.x += playerSpeed
    }

    if (Gdx.input.isKeyPressed(Input.Keys.W)) {
      speed.y += playerSpeed
    }

    if (Gdx.input.isKeyPressed(Input.Keys.S)) {
      speed.y -= playerSpeed
    }

    if (speed.len() > 0) {
      shared.networkControl.move(speed, shared.me.angle, RUNNING)
    }
  }

  override def onExit(): Unit = {
    Gdx.input.setInputProcessor(null)
  }
}
