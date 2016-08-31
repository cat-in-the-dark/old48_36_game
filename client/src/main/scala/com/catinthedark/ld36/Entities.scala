package com.catinthedark.ld36

import java.util.UUID

import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.catinthedark.common.Const.{Balance, UI}
import com.catinthedark.ld36.Assets.Animations.{FanAnimationPack, PlayerSkin}
import com.catinthedark.ld36.Assets.{Animations, Textures}
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
                      var skin: PlayerSkin = Animations.gopSkins(UI.randomSkin), // it'll be overwritten by server, place pack here just to avoid NPE
                      var hasArmor: Boolean) extends Entity {
  var animationCounter = 0f

  override def texture(delta: Float) = {
    state match {
      case IDLE =>
        if(hasBrick) skin.idleWithBrick
        else skin.idle
      case RUNNING =>
        animationCounter += delta * 2
        if (hasBrick) skin.runningWithBrick.getKeyFrame(animationCounter)
        else skin.running.getKeyFrame(animationCounter)
      case KILLED =>
        skin.killed
      case THROWING =>
        animationCounter += delta
        skin.throwing.getKeyFrame(animationCounter)
    }
  }

  override def name: String = "Player"
}


case class Fan(pos: Vector2, animation: FanAnimationPack, var delta: Float = 0, angle: Float = 0, var speed: Float = 1f)

case class Brick(var pos: Vector2,
                 var angle: Float,
                 var id: UUID,
                 radius: Float = Balance.brickRadius) extends Entity {
  override def texture(delta: Float): TextureRegion = new TextureRegion(Textures.brick)

  override def name: String = "Brick"
}

sealed trait Bonus {
  val id: UUID
  var pos: Vector2
  def texture(delta: Float = 0): TextureRegion
}

case class HatBonus(id: UUID, var pos: Vector2) extends Bonus {
  override def texture(delta: Float): TextureRegion = new TextureRegion(Textures.kepa)
}