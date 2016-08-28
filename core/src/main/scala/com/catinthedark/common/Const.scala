package com.catinthedark.common

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
  }

  object HUD {
  }

  object Balance {
    val roundTime: Long = 60
    val shootRageSpeed = 2f
    val maxShootRage = 1f
    val playerSpeed = 5.0f
    val playerSpeedBonus = 10.0f
    val playerRadius = 100.0f
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
  }
}
