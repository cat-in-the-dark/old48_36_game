package com.catinthedark.ld36.common

case class Stat(username: String, scores: Int, dead: Int)
case class Stats(me: Stat, other: Seq[Stat])
