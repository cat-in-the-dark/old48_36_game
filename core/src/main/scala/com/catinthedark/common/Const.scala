package com.catinthedark.common

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2

import scala.util.Random

object Const{
  val debugEnabled = false

  object UI {
    val animationSpeed = 0.2f
    val darknessRed = 0.04f
    var darknessGreen = 0.04f
    var darknessBlue = 0.157f

    val throwBrickAnimationSpeed = 0.05f

    val horizontalBorderWidth = 80f
    val verticalBorderWidth = 40f
    val fieldWidth = 1081f
    val fieldHeight = 652f
  }

  object HUD {
  }

  object Balance {
    val roundTime: Long = 20
    val shootRageSpeed = 2f
    val maxShootRage = 1f
    val playerSpeed = 5.0f
    val playerSpeedBonus = 10.0f
    val playerRadius = 40.0f
    val brickRadius = 10.0f
    val spawnPoints = Array(
      new Vector2(100,100),
      new Vector2(500, 500))

    def randomSpawn = {
      val ab = spawnPoints(new Random().nextInt(spawnPoints.length))
      ab.cpy()
    }
  }

  object Projection {
    val width = 1161F
    val height = 652F
    val mapLeftBorder = 665f
    val mapTopBorder = 400f

    def calcX(screenX: Int): Int = (screenX.toFloat * Const.Projection.width / Gdx.graphics.getWidth).toInt
    def calcY(screenY: Int): Int = (screenY.toFloat * Const.Projection.height / Gdx.graphics.getHeight).toInt
  }
}
