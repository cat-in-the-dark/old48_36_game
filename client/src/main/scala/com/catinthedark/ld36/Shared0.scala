package com.catinthedark.ld36

import java.net.URI

import com.badlogic.gdx.math.Vector2
import com.catinthedark.ld36.Assets.Animations.gopAnimationPack
import com.catinthedark.common.Const.Balance
import com.catinthedark.ld36.network.{NetworkControl, NetworkWSControl}
import com.catinthedark.models.{KILLED, THROWING, RUNNING, IDLE}

import scala.collection.mutable

case class Shared0(serverAddress: URI,
                   me: PlayerView = new PlayerView(new Vector2(0, 0), IDLE, gopAnimationPack, 0.0f, Balance.playerRadius, hasBrick = true),
                   enemies: mutable.ListBuffer[PlayerView] = new mutable.ListBuffer[PlayerView],
                   bricks: mutable.ListBuffer[Brick] = new mutable.ListBuffer[Brick],
                   var shootRage: Float = 0) {
  val networkControl: NetworkControl = new NetworkWSControl(serverAddress)
  private var networkControlThread: Thread = _

  def stopNetwork(): Unit = {
    networkControl.dispose()
    if (networkControlThread != null) {
      networkControlThread.interrupt()
      networkControlThread = null
    }
    println("Network stopped")
  }

  def start(): Unit = {
    stopNetwork()
    networkControlThread = new Thread(networkControl)
    networkControlThread.start()
    println("Network thread started")
  }
}
