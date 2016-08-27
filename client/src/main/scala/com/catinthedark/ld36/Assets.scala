package com.catinthedark.ld36

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.{Animation, TextureRegion}
import com.badlogic.gdx.{Gdx, utils}
import com.catinthedark.ld36.common.Const
import com.catinthedark.ld36.common.Const.UI
import com.catinthedark.models.SoundNames

object Assets {
  object Maps {
  }
  object Shaders {
  }
  object Textures {
    val brick = new Texture(Gdx.files.internal("textures/brick.png"))
    val field = new Texture(Gdx.files.internal("textures/field.png"))
    val gop = new Texture(Gdx.files.internal("textures/gop.png"))
    val logo = new Texture(Gdx.files.internal("textures/logo.png"))

    val gopFrames = TextureRegion.split(gop, 100, 100)
  }

  object Fonts {
    val mainGenerator = new FreeTypeFontGenerator(Gdx.files.internal("font/main.ttf"))
    val enterName = {
      val params = new FreeTypeFontParameter()
      params.size = 44
      val font = mainGenerator.generateFont(params)
      font.setColor(224f / 255, 248f / 255, 18f / 255, 1)
      font
    }

    val statsMain = {
      val params = new FreeTypeFontParameter()
      params.size = 44
      val font = mainGenerator.generateFont(params)
      font.setColor(0f / 255, 255f / 255, 0 / 255, 1)
      font
    }

    val statsMain2 = {
      val params = new FreeTypeFontParameter()
      params.size = 20
      val font = mainGenerator.generateFont(params)
      font.setColor(255f / 255, 255f / 255, 255 / 255, 1)
      font
    }

    val statsEntry = {
      val params = new FreeTypeFontParameter()
      params.size = 20
      val font = mainGenerator.generateFont(params)
      font.setColor(255f / 255, 0f / 255, 0 / 255, 1)
      font
    }
  }

  object Animations {
    private def loopingAnimation(frames: Array[Array[TextureRegion]], frameIndexes: (Int, Int)*): Animation = {
      val array = new utils.Array[TextureRegion]
      frameIndexes.foreach(i => array.add(frames(i._1)(i._2)))
      new Animation(Const.UI.animationSpeed, array, Animation.PlayMode.LOOP)
    }

    private def normalAnimation(speed: Float, frames: Array[Array[TextureRegion]], frameIndexes: (Int, Int)*): Animation = {
      val array = new utils.Array[TextureRegion]
      frameIndexes.foreach(i => array.add(frames(i._1)(i._2)))
      new Animation(speed, array, Animation.PlayMode.NORMAL)
    }

    trait PlayerAnimationPack {
      val idle: TextureRegion
      val running: Animation
      val killed: TextureRegion
      val throwing: Animation
    }

    object gopAnimationPack extends PlayerAnimationPack {
      override val idle: TextureRegion = Textures.gopFrames(0)(0)
      override val running: Animation = loopingAnimation(Textures.gopFrames, (0, 1))
      override val killed: TextureRegion = Textures.gopFrames(0)(2)
      override val throwing: Animation = normalAnimation(UI.throwBrickAnimationSpeed, Textures.gopFrames, (0, 3))
    }
  }

  object Audios {
    val soundMap = Map(
      SoundNames.ChponkSuka -> Gdx.audio.newSound(Gdx.files.internal("sound/chponk_suka.mp3")),
      SoundNames.HeadShot -> Gdx.audio.newSound(Gdx.files.internal("sound/head_shot.mp3")),
      SoundNames.Tooth -> Gdx.audio.newSound(Gdx.files.internal("sound/zuby_po_vsey_ulitse.mp3")),
      SoundNames.Siklo -> Gdx.audio.newSound(Gdx.files.internal("sound/siklo.mp3"))
    )
  }
}
