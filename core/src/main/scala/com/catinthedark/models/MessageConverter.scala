package com.catinthedark.models

import com.catinthedark.lib.network.JacksonConverterScala

object MessageConverter {
  def convertStateToString(state: State): String = {
    state match {
      case IDLE => "IDLE"
      case RUNNING => "RUNNING"
      case KILLED => "KILLED"
      case THROWING => "THROWING"
    }
  }

  def convertStringToState(state: String): State = {
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
    }).registerConverter[DisconnectedMessage](classOf[DisconnectedMessage], data => {
      converter.objectMapper.convertValue(data, classOf[DisconnectedMessage])
    }).registerConverter[MoveMessage](classOf[MoveMessage], data => {
      converter.objectMapper.convertValue(data, classOf[MoveMessage])
    })

    println(s"Converters ${converter.registeredConverters}")
  }
}
