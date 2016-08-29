package com.catinthedark.lib

import com.badlogic.gdx.math.Vector2

object MathUtilsLib {
  def roundVector(v: Vector2): Vector2 = {
    new Vector2(Math.round(v.x).toFloat, Math.round(v.y).toFloat)
  }

  def lerp(start: Vector2, end: Vector2, alpha: Float): Vector2 = {
    val invAlpha: Float = 1.0f - alpha
    new Vector2(
      (start.x * invAlpha) + (end.x * alpha),
      (start.y * invAlpha) + (end.y * alpha)
    )
  }
}
