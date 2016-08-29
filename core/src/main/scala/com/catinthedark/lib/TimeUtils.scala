package com.catinthedark.lib

object TimeUtils {
  def currentTimeInSeconds: Double = System.currentTimeMillis().toDouble / 1000.0
}
