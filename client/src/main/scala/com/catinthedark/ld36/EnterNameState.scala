package com.catinthedark.yoba

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.{Gdx, Input, InputAdapter}
import com.catinthedark.ld36.Assets
import com.catinthedark.lib.Magic._
import com.catinthedark.lib.YieldUnit

/**
  * Created by over on 23.08.15.
  */
class EnterNameState extends YieldUnit[Any, String] {
  override def toString: String = "EnterNameState"

  val batch = new SpriteBatch
  var username = "anon"
  var done = false
  var state = 0f

  override def onActivate(none: Any): Unit = {
    username = "anon"
    done = false
    Gdx.input.setInputProcessor(new InputAdapter {

      override def keyDown(keycode: Int): Boolean = {
        if (keycode == Input.Keys.BACKSPACE) {
          val newLength = username.length - 1
          username = username.substring(0, if (newLength > 0) newLength else 0)
        }

        if (keycode == Input.Keys.ENTER)
          if (username != "")
            done = true

        true
      }

      override def keyTyped(character: Char): Boolean = {
        if (character >= 'A' && character <= 'Z' ||
          character >= 'a' && character <= 'z' ||
          character >= '0' && character <= '9')
          username += character
        println(username)
        true
      }
    })
  }

  override def run(delta: Float): Option[String] = {
    state += delta
    val suffix = if (state - state.toInt < 0.5) "_" else ""

    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT)
    batch.managed { self: SpriteBatch =>
      //Assets.Fonts.timerFrontFont.draw(self, "NEW RECORD!", 300, 500)
      Assets.Fonts.enterName.draw(self, "ENTER YOUR NAME", 300, 400)
      Assets.Fonts.enterName.draw(self, s"${username}${suffix}", 300, 300)
    }

    if (done) Some(username)
    else None
  }

  override def onExit(): Unit = {
    println(s"username -> $username")
    Gdx.input.setInputProcessor(null)
  }
}
