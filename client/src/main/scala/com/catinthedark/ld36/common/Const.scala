package com.catinthedark.ld36.common

import com.badlogic.gdx.Gdx
import com.catinthedark.lib.constants.ConstDelegate

object Const extends ConstDelegate {
  override def delegate = Seq(
    debugEnabled
  )

  val debugEnabled = onOff("debug render", false)

  object UI {
    val animationSpeed = 0.2f
    val darknessRed = 0.04f
    var darknessGreen = 0.04f
    var darknessBlue = 0.157f

    val throwBrickAnimationSpeed = 0.1f
  }

  object HUD {
  }

  object Balance {
    val shootRageSpeed = 2f
    val maxShootRage = 1f
    val playerSpeed = 5.0f
    val playerSpeedBonus = 10.0f
    val playerRadius = 100.0f
    val brickRadius = 10.0f
  }

  object Projection {
    val width = 1161F
    val height = 652F
  }
}
