package com.catinthedark.models

import com.catinthedark.lib.network.JacksonConverterScala

object MessageConverter {
  def stateToString(state: State): String = {
    state match {
      case IDLE => "IDLE"
      case RUNNING => "RUNNING"
      case KILLED => "KILLED"
      case THROWING => "THROWING"
    }
  }

  def stringToState(state: String): State = {
    state match {
      case "IDLE" => IDLE
      case "RUNNING" => RUNNING
      case "KILLED" => KILLED
      case "THROWING" => THROWING
    }
  }

  def registerConverters(converter: JacksonConverterScala): Unit = {
    converter
      .registerMessage(classOf[ServerHelloMessage])
      .registerMessage(classOf[HelloMessage])
      .registerMessage(classOf[GameStartedMessage])
      .registerMessage(classOf[EnemyDisconnectedMessage])
      .registerMessage(classOf[MoveMessage])
      .registerMessage(classOf[GameStateMessage])
      .registerMessage(classOf[RoundEndsMessage])
      .registerMessage(classOf[SoundMessage])
      .registerMessage(classOf[ThrowBrickMessage])

    println(s"Converters ${converter.registeredConverters}")
  }
}
