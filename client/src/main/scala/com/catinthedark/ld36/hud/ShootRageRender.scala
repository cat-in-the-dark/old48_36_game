package com.catinthedark.ld36.hud

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType
import com.catinthedark.common.Const
import com.catinthedark.ld36.Assets
import com.catinthedark.ld36.common.Stats

class ShootRageRender {
  val shapeRenderer = new ShapeRenderer
  shapeRenderer.setColor(1, 1, 1, 0.5f)

  val barSize = 200f
  val barPosX = 900f
  val barPosY = 20f


  def render(rage: Float) = {
    shapeRenderer.begin(ShapeType.Filled)
    shapeRenderer.setColor(1, 1, 1, 1)
    shapeRenderer.rect(barPosX - 5, barPosY - 5, 210, 30)

    if (rage > Const.Balance.maxShootRage * 0.9) {
      shapeRenderer.setColor(1, 0, 0, 1)
      shapeRenderer.rect(barPosX, barPosY, barSize * rage, 20)
    }
    if (rage > Const.Balance.maxShootRage * 0.45) {
      shapeRenderer.setColor(1, 1, 0, 1)
      shapeRenderer.rect(barPosX, barPosY, barSize * Math.min(rage, 0.9f), 20)
    }

    shapeRenderer.setColor(0, 1, 0, 1)
    shapeRenderer.rect(barPosX, barPosY, barSize * Math.min(rage, 0.45f), 20)

    shapeRenderer.end()
  }

}
