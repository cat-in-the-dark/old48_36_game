package com.catinthedark.server.persist

import java.util.{Date, UUID}

import com.catinthedark.server.GeoModel

case class PlayerModel(uuid: UUID, name: String, ip: String, geo: GeoModel, connectedAt: Date, disconnectedAt: Date, frags: Long, deaths: Long)
case class GameModel(players: List[UUID], finishedAt: Date)