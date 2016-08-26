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
  }

  object HUD {
  }


  object Projection {
    val width = 1161F
    val height = 652F
    val mapWidth = 3200f
    val mapHeight = 3200f

    def calcX(screenX: Int): Int = (screenX.toFloat * Const.Projection.width / Gdx.graphics.getWidth).toInt
    def calcY(screenY: Int): Int = (screenY.toFloat * Const.Projection.height / Gdx.graphics.getHeight).toInt
  }

  object Balance {
  }
}
