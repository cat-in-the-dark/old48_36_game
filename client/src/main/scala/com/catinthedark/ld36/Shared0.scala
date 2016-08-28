package com.catinthedark.ld36

import java.net.URI
import java.util.UUID

import com.badlogic.gdx.math.Vector2
import com.catinthedark.ld36.Assets.Animations.gopAnimationPack
import com.catinthedark.common.Const.Balance
import com.catinthedark.ld36.network.{NetworkControl, NetworkWSControl}
import com.catinthedark.models.{IDLE, KILLED, RUNNING, THROWING}

import scala.collection.mutable

case class Shared0(serverAddress: URI,
                   enemies: mutable.ListBuffer[PlayerView] = new mutable.ListBuffer[PlayerView],
                   bricks: mutable.ListBuffer[Brick] = new mutable.ListBuffer[Brick],
                   var me: PlayerView = new PlayerView(new Vector2(0, 0), IDLE, 0.0f, null, false),
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
