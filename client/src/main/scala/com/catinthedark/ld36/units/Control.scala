package com.catinthedark.ld36.units

import com.badlogic.gdx.{Input, Gdx}
import com.catinthedark.ld36.Shared0
import com.catinthedark.ld36.common.Const
import com.catinthedark.lib.SimpleUnit
import Const.Balance

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

  override def run(delta: Float) = {
    controlShoot(delta)
  }

}
