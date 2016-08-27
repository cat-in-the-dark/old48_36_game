package com.catinthedark.ld36

import java.net.URI

import com.badlogic.gdx.{Gdx, Input, InputAdapter}
import com.catinthedark.lib.YieldUnit

class ConnectState(address: String) extends YieldUnit[String, Shared0] {
  var shared0: Shared0 = _
  var hardSkip: Boolean = false

  override def onActivate(data: String): Unit = {
    shared0 = Shared0(new URI(address))

    Gdx.input.setInputProcessor(new InputAdapter {
      override def keyDown(keyCode: Int): Boolean = {
        keyCode match {
          case Input.Keys.BACKSPACE => hardSkip = true
          case _ =>
        }
        true
      }
    })

    shared0.start()

    shared0.networkControl.onServerHello.ports += onServerHello

    def onServerHello(u: Unit): Unit = {
      shared0.networkControl.hello(data)
    }
  }

  override def onExit(): Unit = {
  }

  override def run(delta: Float): Option[Shared0] = {
    if (hardSkip) {
      hardSkip = false
      println("WARNING hard skip of network connection")
      return Some(shared0)
    }

    if (shared0 != null && shared0.networkControl.isConnected.isDefined) {
      Some(shared0)
    } else {
      None
    }
  }
}
