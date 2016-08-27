package com.catinthedark.models

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
}
