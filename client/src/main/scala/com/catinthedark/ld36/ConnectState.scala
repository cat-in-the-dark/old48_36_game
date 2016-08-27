package com.catinthedark.ld36

import com.catinthedark.lib.YieldUnit

class ConnectState(address: String) extends YieldUnit[String, Shared0] {
  override def onActivate(data: String): Unit = {
    //open connection here?
  }

  override def onExit(): Unit = {

  }

  override def run(delta: Float): Option[Shared0] = {
    Some(new Shared0(null))
  }
}
