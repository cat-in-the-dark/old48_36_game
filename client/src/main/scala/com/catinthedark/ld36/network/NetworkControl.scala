package com.catinthedark.ld36.network

import java.util.concurrent.ConcurrentLinkedQueue

import com.badlogic.gdx.math.Vector2
import com.catinthedark.lib.Pipe
import com.catinthedark.models.{HelloMessage, JumpMessage, MoveMessage, ShootMessage}

trait NetworkControl extends Runnable {
  var isConnected: Option[Unit] = None
  var isMain: Boolean = false

  val onMovePipe = new Pipe[(Vector2, Float, Boolean)]()
  val onShootPipe = new Pipe[(Vector2, String)]()
  val onJumpPipe = new Pipe[(Vector2, Float, Float)]()
  val onEnemyDisconnected = new Pipe[Unit]()

  def hello(name: String): Unit = {
    processOut(HelloMessage(name))
  }

  def move(pos: Vector2, angle: Float, idle: Boolean): Unit = {
    processOut(MoveMessage(x=pos.x, y=pos.y, angle = angle, idle = idle))
  }

  def shoot(shotFrom: Vector2, objName: String): Unit = {
    processOut(ShootMessage(x = shotFrom.x, y = shotFrom.y, shotObject = objName))
  }

  def jump(pos: Vector2, angle: Float, scale: Float): Unit = {
    processOut(JumpMessage(x = pos.x, y = pos.y, angle = angle, scale = scale))
  }

  def processIn() = {
    while(!bufferIn.isEmpty)
      bufferIn.poll()()
  }
  
  def processOut(message: Any)
  
  def dispose(): Unit = {
    isConnected = None
  }

  protected val bufferIn = new ConcurrentLinkedQueue[() => Unit]()
  
  protected def onMove(msg: (Vector2, Float, Boolean)) = bufferIn.add(() => onMovePipe(msg))
  protected def onShoot(objName: String, shotFrom: Vector2) = bufferIn.add(() => onShootPipe(shotFrom, objName))
  protected def onJump(msg: (Vector2, Float, Float)) = bufferIn.add(() => onJumpPipe(msg))
  protected def onGameStarted(msg: (String, String)) = println(s"Received GameStart package $msg")
}
