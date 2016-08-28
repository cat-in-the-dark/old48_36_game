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

  val names = Seq("Anon", "Anonas", "Anton", "Antonina", "Nanas", "Adidos", "Pipos", "Mimos", "Pisos", "Anonim", "Antonim", "Zasos", "Abibas", "Bibos", "Poltos")
  def defaultName = names(new Random().nextInt(names.length))

  object Balance {
    val hatRadius = 20f
    val roundTime: Long = 120
    val shootRageSpeed = 10f
    val maxShootRage = 80f
    val minShootRange = 20f
    val playerSpeed = 5.0f
    val playerSpeedBonus = 10.0f
    val playerRadius = 40.0f
    val brickRadius = 10.0f
    val spawnPoints: List[Vector2] = 2.to(9).flatMap(x => {
      1.to(6).map(y => {
        new Vector2(x*100, y*100)
      })
    }).toList

    val spawnBrickPoints = Array(
      new Vector2(200, 200),
      new Vector2(400, 100),
      new Vector2(120, 500)
    )

    def randomSpawn = {
      val ab = spawnPoints(new Random().nextInt(spawnPoints.length))
      ab.cpy()
    }

    val bonusDelay = 20L
    val bonusesAtOnce = 2
    def randomBonus = {
      Bonus.hat
    }

    def randomBrickSpawn = {
      val ab = spawnBrickPoints(new Random().nextInt(spawnBrickPoints.length))
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
