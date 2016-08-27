package com.catinthedark.models

import com.catinthedark.lib.network.{JacksonConverterScala, NetworkTransport}
import com.catinthedark.lib.network.NetworkTransport.Converter

/**
  * Created by kirill on 27.08.16.
  */
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
    converter.registerConverter[ServerHelloMessage](classOf[ServerHelloMessage], data => {
      converter.objectMapper.convertValue(data, classOf[ServerHelloMessage])
    })
    converter.registerConverter[HelloMessage](classOf[HelloMessage], data => {
      converter.objectMapper.convertValue(data, classOf[HelloMessage])
    })

    println(s"Converters ${converter.registeredConverters}")
  }
}
