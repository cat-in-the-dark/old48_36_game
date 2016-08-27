package com.catinthedark.ld36

/**
  * Created by kirill on 27.08.16.
  */
sealed trait State
object IDLE extends State
object RUNNING extends State
object KILLED extends State
object THROWING extends State
