package com.catinthedark.lib

import java.util.concurrent.{Executors, TimeUnit}

class Intervals(val threadCount: Int = 1) {
  private val executor = Executors.newScheduledThreadPool(threadCount)

  def repeat(delay: Long, timeUnit: TimeUnit, callback: () => Unit): Unit = {
    executor.scheduleWithFixedDelay(new Runnable {
      override def run() = callback()
    }, delay, delay, timeUnit)
  }

  def deffer(delay: Long, timeUnit: TimeUnit, callback: () => Unit): Unit = {
    executor.schedule(new Runnable {
      override def run() = callback()
    }, delay, timeUnit)
  }

  def shutdown(): Unit ={
    println("Shutdown")
    executor.shutdown()
  }
}
