package com.catinthedark.ld36

import java.net.URI

import com.catinthedark.ld36.network.{NetworkControl, NetworkWSControl}

class Shared0(
  val serverAddress: URI
) {
  val networkControl: NetworkControl = new NetworkWSControl(serverAddress)
  private var networkControlThread: Thread = null
  
  def stopNetwork(): Unit = {
    networkControl.dispose()
    if (networkControlThread != null) {
      networkControlThread.interrupt()
      networkControlThread = null
    }
    println("Network stopped")
  }
  
  def start(): Unit = {
    stopNetwork()
    networkControlThread = new Thread(networkControl)
    networkControlThread.start()
    println("Network thread started")
  }
}