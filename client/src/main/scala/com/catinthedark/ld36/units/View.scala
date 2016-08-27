package com.catinthedark.ld36.units

import com.catinthedark.ld36.{Assets, Player, Shared1}
import com.catinthedark.ld36.common.Const
import com.catinthedark.lib.{MagicSpriteBatch, SimpleUnit}

/**
  * Created by kirill on 27.08.16.
  */
class View(val shared: Shared1) extends SimpleUnit {
  val magicBatch = new MagicSpriteBatch(Const.debugEnabled())

  def drawField(): Unit = {
    magicBatch.managed { self =>
      self.draw(Assets.Textures.field, 0, 0, Assets.Textures.field.getWidth, Assets.Textures.field.getHeight)
    }
  }

  override def run(delta: Float): Unit = {
    drawField()
  }
}
