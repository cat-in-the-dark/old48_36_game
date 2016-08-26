package com.catinthedark.ld36

import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.catinthedark.lib.Magic._
import com.catinthedark.lib.{KeyAwaitState, Stub}

class GameOverState(val shared: Shared0)  extends Stub("GameOver")  with KeyAwaitState {
  override val keycode: Int = Input.Keys.ENTER

  val batch = new SpriteBatch

  override def onActivate(data: Any): Unit = {
    super.onActivate()
  }

  override def run(delta: Float): (Option[Unit], Any) = {
    batch.managed { self: SpriteBatch =>
      self.draw(Assets.Textures.loose, 0, 0)
    }
    super.run(delta)
  }
}
