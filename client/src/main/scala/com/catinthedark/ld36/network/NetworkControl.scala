package com.catinthedark.ld36.network

import java.util.concurrent.ConcurrentLinkedQueue

import com.badlogic.gdx.math.Vector2
import com.catinthedark.lib.Pipe
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

  def hello(name: String): Unit = {
    processOut(HelloMessage(name))
  }

  def move(speed: Vector2, angle: Float, state: State): Unit = {
    processOut(MoveMessage(speed.x, speed.y, angle, MessageConverter.stateToString(state)))
  }

  def processIn() = {
    while(!bufferIn.isEmpty)
      bufferIn.poll()()
  }
  
  def processOut(message: Message)
  
  def dispose(): Unit = {
    isConnected = None
  }

  protected val bufferIn = new ConcurrentLinkedQueue[() => Unit]()
  
  protected def onMove(msg: (Vector2, Float, Boolean)) = bufferIn.add(() => onMovePipe(msg))
  protected def onShoot(objName: String, shotFrom: Vector2) = bufferIn.add(() => onShootPipe(shotFrom, objName))
  protected def onJump(msg: (Vector2, Float, Float)) = bufferIn.add(() => onJumpPipe(msg))
  protected def onGameStarted(msg: (String)) = println(s"Received GameStart package $msg")
  protected def onGameState(gameState: (GameStateModel)) = {
    bufferIn.add(() => onGameStatePipe(gameState))
  }
}
