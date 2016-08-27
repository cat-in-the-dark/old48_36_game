package com.catinthedark.ld36.units

import com.badlogic.gdx.{Gdx, Input}
import com.catinthedark.ld36.Shared0
import com.catinthedark.lib.SimpleUnit
import com.badlogic.gdx.math.Vector2
import com.catinthedark.common.Const
import com.catinthedark.common.Const.Balance
import com.catinthedark.models.RUNNING

/**
  * Created by kirill on 27.08.16.
  */
class Control(shared: Shared0) extends SimpleUnit {

  def controlShoot(delta: Float) = {
    if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
      shared.shootRage = Math.min(shared.shootRage + Balance.shootRageSpeed * delta, Balance.maxShootRage)
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
}
