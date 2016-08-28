package com.catinthedark.ld36

import java.util.UUID

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.catinthedark.common.Const.Balance
import com.catinthedark.ld36.Assets.Animations.{FanAnimationPack, PlayerAnimationPack, gopAnimationPack}
import com.catinthedark.ld36.Assets.Textures
import com.catinthedark.models._

/**
  * Created by kirill on 27.08.16.
  */

sealed trait Entity {
  var pos: Vector2
  val radius: Float
  var angle: Float

  def texture(delta: Float = 0): TextureRegion

  def name: String
}

case class PlayerView(var pos: Vector2,
                      var state: State,
                      var angle: Float,
                      var id: UUID,
                      var hasBrick: Boolean,
                      radius: Float = Balance.playerRadius,
                      pack: PlayerAnimationPack = gopAnimationPack,
                      hasArmor: Boolean) extends Entity {
  var animationCounter = 0f

  override def texture(delta: Float) = {
    state match {
      case IDLE =>
        pack.idle
      case RUNNING =>
        animationCounter += delta * 2
        if (hasBrick) pack.runningWithBrick.getKeyFrame(animationCounter)
        else pack.running.getKeyFrame(animationCounter)
      case KILLED =>
        pack.killed
      case THROWING =>
        animationCounter += delta
        pack.throwing.getKeyFrame(animationCounter)
    }
  }

  override def name: String = "Player"
}


case class Fan(pos: Vector2, animation: FanAnimationPack, var delta: Float = 0, angle: Float = 0, var speed: Float = 1f)

case class Brick(var pos: Vector2, var angle: Float, val radius: Float) extends Entity {
  override def texture(delta: Float): TextureRegion = new TextureRegion(Textures.brick)

  override def name: String = "Brick"
}
