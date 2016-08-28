package com.catinthedark.ld36

import java.net.URI
import java.util.UUID

import com.badlogic.gdx.math.Vector2
import com.catinthedark.ld36.Assets.Animations.gopAnimationPack
import com.catinthedark.common.Const.Balance
import com.catinthedark.ld36.network.{NetworkControl, NetworkWSControl}
import com.catinthedark.models._

import scala.collection.mutable
import scala.util.Random

case class Shared0(serverAddress: URI,
                   enemies: mutable.ListBuffer[PlayerView] = new mutable.ListBuffer[PlayerView],
                   bricks: mutable.ListBuffer[Brick] = new mutable.ListBuffer[Brick],
                   var gameState: GameStateModel = null,
                   var timeRemains: Long = 0,
                   var me: PlayerView = PlayerView(new Vector2(0, 0), IDLE, 0.0f, null, false),
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

  val rand = new Random()

  def tripletX(x: Int, y: Int, angle: Float) =
    Seq(
      Fan(new Vector2(x, y), Assets.Animations.blueFanAnimationPack, angle = angle),
      Fan(new Vector2(x + 100, y), Assets.Animations.blackFanAnimationPack, angle = angle),
      Fan(new Vector2(x + 50, y), Assets.Animations.redFanAnimationPack, angle = angle)
    )

  def duetX(x: Int, y: Int, angle: Float) =
    Seq(
      Fan(new Vector2(x, y), Assets.Animations.blueFanAnimationPack, angle = angle),
      Fan(new Vector2(x + 50, y), Assets.Animations.blackFanAnimationPack, angle = angle)
    )

  def quartetX(x: Int, y: Int, angle: Float) =
    Seq(
      Fan(new Vector2(x, y + rand.nextInt(10)), Assets.Animations.blueFanAnimationPack, angle = angle),
      Fan(new Vector2(x + 100, y), Assets.Animations.blueFanAnimationPack, angle = angle),
      Fan(new Vector2(x + 50, y), Assets.Animations.blackFanAnimationPack, angle = angle),
      Fan(new Vector2(x + 150, y), Assets.Animations.redFanAnimationPack, angle = angle)
    )

  def tripletY(x: Int, y: Int, angle: Float) =
    Seq(
      Fan(new Vector2(x, y), Assets.Animations.blueFanAnimationPack, angle = angle),
      Fan(new Vector2(x, y + 100), Assets.Animations.blackFanAnimationPack, angle = angle),
      Fan(new Vector2(x, y + 50), Assets.Animations.redFanAnimationPack, angle = angle)
    )

  def duetY(x: Int, y: Int, angle: Float) =
    Seq(
      Fan(new Vector2(x, y), Assets.Animations.blueFanAnimationPack, angle = angle),
      Fan(new Vector2(x, y + 50), Assets.Animations.redFanAnimationPack, angle = angle)
    )

  val fans = tripletX(200, 25, 0) ++ duetX(400, 25, 0) ++ duetX(500, 25, 0) ++ quartetX(600, 25, 0) ++ duetX(900, 25, 0) ++
    tripletY(60, 200, -90) ++ tripletY(60, 340, -90) ++ tripletY(60, 560, -90) ++
    tripletX(150, 695, 180) ++ duetX(400, 695, 180) ++ quartetX(600, 695, 180) ++ duetX(900, 695, 180) ++ duetX(1100, 695, 180) ++
    tripletY(1170, 100, 90) ++ tripletY(1170, 440, 90)

  fans.foreach { fan =>
    fan.delta = rand.nextFloat() * 2
    fan.pos.x += 5 - rand.nextInt(10)
    fan.pos.y += 5 - rand.nextInt(10)
    fan.speed = rand.nextFloat()
  }

  var fansRageMode = false
}
