package com.catinthedark.ld36.network

import java.util.concurrent.{ConcurrentLinkedQueue, TimeUnit}

import com.badlogic.gdx.math.Vector2
import com.catinthedark.common.Const
import com.catinthedark.lib.{Intervals, Pipe}
import com.catinthedark.models._

trait NetworkControl extends Runnable {
  var isConnected: Option[Unit] = None
  var isMain: Boolean = false

  val onMovePipe = new Pipe[(Vector2, Float, Boolean)]()
  val onShootPipe = new Pipe[(Vector2, String)]()
  val onJumpPipe = new Pipe[(Vector2, Float, Float)]()
  val onEnemyDisconnected = new Pipe[Unit]()
  val onServerHello = new Pipe[Unit]()
  val onGameStatePipe = new Pipe[GameStateModel]()
  val onRoundEndsPipe = new Pipe[GameStateModel]()

  var executor: Intervals = _
  var moveMessage: MoveMessage = _
  var throwBrickMessage: ThrowBrickMessage = _

  override def run(): Unit = {
    executor = new Intervals(1)
    executor.repeat(Const.Networking.tickDelay, TimeUnit.MILLISECONDS, () => {

      if (moveMessage != null) {
        processOut(moveMessage)
        moveMessage = null
      }

      if (throwBrickMessage != null) {
        processOut(throwBrickMessage)
        throwBrickMessage = null
      }
    })
  }

  def hello(name: String): Unit = {
    processOut(HelloMessage(name))
  }

  def move(speed: Vector2, angle: Float, state: State): Unit = {
    if (moveMessage == null) {
      moveMessage = MoveMessage(
        speed.x, speed.y, angle,
        MessageConverter.stateToString(state))
    } else {
      moveMessage = moveMessage.copy(
        moveMessage.speedX + speed.x,
        moveMessage.speedY + speed.y,
        angle, MessageConverter.stateToString(state))
    }
  }

  def throwBrick(pos: Vector2, force: Float, angle: Float): Unit = {
     throwBrickMessage = ThrowBrickMessage(pos.x, pos.y, force, angle)
  }

  def processIn() = {
    while(!bufferIn.isEmpty)
      bufferIn.poll()()
  }
  
  def processOut(message: Message)
  
  def dispose(): Unit = {
    isConnected = None
    if (executor != null) executor.shutdown()
  }

  protected val bufferIn = new ConcurrentLinkedQueue[() => Unit]()

  protected def onGameStarted(msg: (String)) = println(s"Received GameStart package $msg")
  protected def onGameState(gameState: (GameStateModel)) = bufferIn.add(() => onGameStatePipe(gameState))
  protected def onRoundEnds(gameState: (GameStateModel)) = bufferIn.add(() => onRoundEndsPipe(gameState))
}
