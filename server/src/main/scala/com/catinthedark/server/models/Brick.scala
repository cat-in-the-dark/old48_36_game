package com.catinthedark.server.models

import com.catinthedark.models.BrickModel

/**
  * Created by kirill on 28.08.16.
  */
case class Brick(
  var initialSpeed: Float,
  var currentSpeed: Float,
  entity: BrickModel
) {
}
