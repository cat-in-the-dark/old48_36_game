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
    //TODO: can be removed, but needs big refactoring work
    converter.registerConverter[ServerHelloMessage](classOf[ServerHelloMessage], data => {
      converter.objectMapper.convertValue(data, classOf[ServerHelloMessage])
    }).registerConverter[HelloMessage](classOf[HelloMessage], data => {
      converter.objectMapper.convertValue(data, classOf[HelloMessage])
    }).registerConverter[GameStartedMessage](classOf[GameStartedMessage], data => {
      converter.objectMapper.convertValue(data, classOf[GameStartedMessage])
    }).registerConverter[EnemyDisconnectedMessage](classOf[EnemyDisconnectedMessage], data => {
      converter.objectMapper.convertValue(data, classOf[EnemyDisconnectedMessage])
    }).registerConverter[MoveMessage](classOf[MoveMessage], data => {
      converter.objectMapper.convertValue(data, classOf[MoveMessage])
    }).registerConverter[GameStateMessage](classOf[GameStateMessage], data => {
      converter.objectMapper.convertValue(data, classOf[GameStateMessage])
    }).registerConverter[RoundEndsMessage](classOf[RoundEndsMessage], date => {
      converter.objectMapper.convertValue(date, classOf[RoundEndsMessage])
    })

    println(s"Converters ${converter.registeredConverters}")
  }
}
