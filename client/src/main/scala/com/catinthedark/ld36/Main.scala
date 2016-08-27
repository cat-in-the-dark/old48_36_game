package com.catinthedark.ld36

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.{Game, Gdx, Input}
import com.catinthedark.ld36.common.{Stats, Stat}
import com.catinthedark.lib._
import com.catinthedark.yoba.EnterNameState

import scala.util.Random

class Main(address: String) extends Game {
  val rm = new RouteMachine()

  def keyAwait(name: String, tex: Texture, key: Int = Input.Keys.ENTER) =
    new Stub(name) with TextureState with KeyAwaitState {
      val texture: Texture = tex
      val keycode: Int = Input.Keys.ENTER
    }

  def delayed(name: String, tex: Texture, _delay: Float) =
    new Stub(name) with TextureState with DelayState {
      val texture: Texture = tex
      val delay: Float = _delay
    }

  val rand = new Random()

  override def create() = {

    val logo = delayed("Logo", Assets.Textures.logo, 1.0f)
    //    val t1 = keyAwait("Tutorial1", Assets.Textures.t1)
    //    val t2 = keyAwait("Tutorial2", Assets.Textures.t2)
    //    val t3 = keyAwait("Tutorial3", Assets.Textures.t3)
    //    val t4 = keyAwait("Tutorial4", Assets.Textures.t4)
    //    val t5 = keyAwait("Tutorial4", Assets.Textures.t5)
    //    val t6 = keyAwait("Tutorial4", Assets.Textures.t6)
    val enterName = new EnterNameState
    val openConnection = new ConnectState(address)
    val game = new GameState
    val scores = new StatsState

    rm.addRoute[Unit](logo, anyway => enterName)
    rm.addRoute[String](enterName, username => openConnection)
    rm.addRoute[Shared0](openConnection, shared => game)
    rm.addRoute[Stats](game, shared => scores)
    rm.addRoute[Unit](scores, none => enterName)

    rm.start(logo)
  }

  override def render() = {
    rm.run(Gdx.graphics.getDeltaTime)
  }

  override def dispose(): Unit = {
  }
}
