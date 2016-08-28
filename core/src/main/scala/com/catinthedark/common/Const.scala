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

  object Bonus {
    val hat = "hat"
  }

  object Balance {
    val roundTime: Long = 20
    val shootRageSpeed = 2f
    val maxShootRage = 1f
    val playerSpeed = 5.0f
    val playerSpeedBonus = 10.0f
    val playerRadius = 40.0f
    val brickRadius = 10.0f
    val spawnPoints: List[Vector2] = 2.to(9).flatMap(x => {
      1.to(6).map(y => {
        new Vector2(x*100, y*100)
      })
    }).toList

    def randomSpawn = {
      val ab = spawnPoints(new Random().nextInt(spawnPoints.length))
      ab.cpy()
    }

    val bonusDelay = 5L
    val bonusesAtOnce = 2
    def randomBonus = {
      Bonus.hat
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
