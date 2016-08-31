package com.catinthedark.server.persist

import com.catinthedark.server.models.Player

import scala.concurrent.Future

trait IRepository {
  def onRoundFinish(players: List[Player]): Future[Unit]

  def onPlayerConnect(player: Player): Future[Unit]

  def onPlayerDisconnect(player: Player): Future[Unit]
}
