package com.catinthedark.ld36.units

import com.badlogic.gdx.{Gdx, Input}
import com.catinthedark.ld36.{Assets, Shared0}
import com.catinthedark.lib.{Deferred, LocalDeferred, SimpleUnit}
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.{Gdx, Input, InputAdapter}
import com.catinthedark.common.Const
import com.catinthedark.common.Const.Balance
import com.catinthedark.models._
import com.catinthedark.ld36.Shared0
import com.catinthedark.lib.{Pipe, SimpleUnit}
import com.catinthedark.models.{IDLE, RUNNING, THROWING}

/**
  * Created by kirill on 27.08.16.
  */
abstract class Control(shared: Shared0) extends SimpleUnit with Deferred {
  val onGameReload = new Pipe[Unit]()

  override def onActivate(): Unit = {
    Gdx.input.setInputProcessor(new InputAdapter {
      override def keyDown(keycode: Int): Boolean = {
        keycode match {
          case Input.Keys.ESCAPE => onGameReload()
          case Input.Keys.R =>
            shared.fansRageMode = true
            Assets.Audios.stadiumNoise.play(0.5f)
            defer(1f, () => shared.fansRageMode = false)
          case _ =>
        }
        true
      }
    })
  }

  def controlShoot(delta: Float) = {
    if (shared.me.hasBrick && shared.me.state == IDLE) {
      if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
        if (shared.shootRage == 0) {
          shared.shootRage = Balance.minShootRange
        } else {
          shared.shootRage = Math.min(shared.shootRage + Balance.shootRageSpeed * delta, Balance.maxShootRage)
        }
      } else if (shared.shootRage != 0) {
        shared.me.animationCounter = 0
        shared.me.state = THROWING
        val brickX = shared.me.pos.x - (Balance.playerRadius + Balance.brickRadius + 1) * Math.sin(Math.toRadians(shared.me.angle)).toFloat
        val brickY = shared.me.pos.y + (Balance.playerRadius + Balance.brickRadius + 1) * Math.cos(Math.toRadians(shared.me.angle)).toFloat
        shared.networkControl.throwBrick(new Vector2(brickX, brickY), shared.shootRage, shared.me.angle)
        Assets.Audios.soundMap(SoundNames.Throw).play()
        defer(0.2f, () => shared.me.state = IDLE)
        shared.shootRage = 0
      }
    } else {
      shared.shootRage = 0
    }
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

    if (shared.me.state != KILLED) {
      if (speed.len() > 0) {
        shared.me.state = RUNNING
      } else {
        shared.me.state = IDLE
      }

      shared.networkControl.move(speed, shared.me.angle, shared.me.state)
    }
  }

  override def onExit(): Unit = {
    Gdx.input.setInputProcessor(null)
  }
}
