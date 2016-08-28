package com.catinthedark.server.models

import java.util.UUID

import com.catinthedark.models.BrickModel

/**
  * Created by kirill on 28.08.16.
  */
case class Brick(
  var initialSpeed: Float,
  var currentSpeed: Float,
  entity: BrickModel,
  var throwerID: UUID = null
) {
}
