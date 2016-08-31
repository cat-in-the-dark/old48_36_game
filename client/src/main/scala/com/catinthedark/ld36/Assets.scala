package com.catinthedark.ld36

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter
import com.badlogic.gdx.graphics.g2d.{Animation, TextureRegion}
import com.badlogic.gdx.{Gdx, utils}
import com.catinthedark.common.Const
import com.catinthedark.common.Const.UI
import com.catinthedark.models.SoundNames

import scala.util.Random

object Assets {
  object Maps {
  }
  object Shaders {
  }
  object Textures {
    val brick = new Texture(Gdx.files.internal("textures/brick.png"))
    val field = new Texture(Gdx.files.internal("textures/gopofon.png"))
    val gopBlack = new Texture(Gdx.files.internal("textures/gop_black.png"))
    val gopBlue = new Texture(Gdx.files.internal("textures/gop_blue.png"))
    val gopGreen = new Texture(Gdx.files.internal("textures/gop_green.png"))
    val gopRed = new Texture(Gdx.files.internal("textures/gop_red.png"))
    val logo = new Texture(Gdx.files.internal("textures/logo.png"))
    val fans = new Texture(Gdx.files.internal("textures/fans.png"))
    val kepa = new Texture(Gdx.files.internal("textures/kepa.png"))
    val menu = new Texture(Gdx.files.internal("textures/menu.png"))
    val t0 = new Texture(Gdx.files.internal("textures/title.png"))
    val fansFrames = TextureRegion.split(fans, 100, 100)

    val kepaRegion = new TextureRegion(kepa)
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

    class PlayerSkin(textureName: String) {
      val frames = TextureRegion.split(new Texture(Gdx.files.internal(s"textures/$textureName.png")), 108, 108)
      val idle: TextureRegion = frames(0)(0)
      val idleWithBrick: TextureRegion = frames(0)(7)
      val running: Animation = loopingAnimation(frames, (0, 0), (0, 1), (0, 2), (0, 3), (0, 4), (0, 5), (0, 6))
      val runningWithBrick: Animation = loopingAnimation(frames, (0, 7), (0, 8), (0, 9), (0, 10), (0, 11), (0, 12), (0, 13))
      val killed: TextureRegion = frames(0)(14)
      val throwing: Animation = normalAnimation(UI.throwBrickAnimationSpeed, frames, (0, 15), (0, 16), (0, 17), (0, 18))
    }

    val gopSkins: Map[String, PlayerSkin] = UI.PlayerSkinNames.map ( skinName => {
      (skinName, new PlayerSkin(skinName))
    }).toMap

    trait FanAnimationPack {
      val normalAnimation: Animation
    }

    object blueFanAnimationPack extends FanAnimationPack {
      override val normalAnimation: Animation = loopingAnimation(Textures.fansFrames, (0, 0), (0, 1))
    }
    object redFanAnimationPack extends FanAnimationPack {
      override val normalAnimation: Animation = loopingAnimation(Textures.fansFrames, (1, 0), (1, 1))
    }
    object blackFanAnimationPack extends FanAnimationPack {
      override val normalAnimation: Animation = loopingAnimation(Textures.fansFrames, (2, 0), (2, 1))
    }
    object girlFanAnimationPack extends FanAnimationPack {
      override val normalAnimation: Animation = loopingAnimation(Textures.fansFrames, (3, 0), (3, 1))
    }
  }

  object Audios {
    val soundMap = Map(
      SoundNames.ChponkSuka -> Gdx.audio.newSound(Gdx.files.internal("sound/chponk_suka.mp3")),
      SoundNames.HeadShot -> Gdx.audio.newSound(Gdx.files.internal("sound/head_shot.mp3")),
      SoundNames.Tooth -> Gdx.audio.newSound(Gdx.files.internal("sound/zuby_po_vsey_ulitse.mp3")),
      SoundNames.Siklo -> Gdx.audio.newSound(Gdx.files.internal("sound/siklo.mp3")),
      SoundNames.Throw -> Gdx.audio.newSound(Gdx.files.internal("sound/throw.mp3"))
    )

    val stadiumNoise = Gdx.audio.newSound(Gdx.files.internal("sound/stadium.mp3"))
    val bgmSteps = Gdx.audio.newMusic(Gdx.files.internal("sound/run.mp3"))
    bgmSteps.setLooping(true)
    bgmSteps.setVolume(0.2f)
    val bgm = Gdx.audio.newMusic(Gdx.files.internal("sound/bgm.mp3"))
    bgm.setLooping(true)
    bgm.setVolume(0.1f)
  }
}
