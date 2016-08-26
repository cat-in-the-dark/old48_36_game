package com.catinthedark.ld36.units

import com.catinthedark.ld36.Shared0
import com.catinthedark.ld36.entity._

import scala.collection.mutable


class Shared1(val shared0: Shared0,
              var entities: mutable.ListBuffer[Entity],
              var isMain: Boolean) {
  def reset() = {
    entities.clear()
  }
}
