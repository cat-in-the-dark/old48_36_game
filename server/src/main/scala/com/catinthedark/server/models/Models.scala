package com.catinthedark.server.models

import java.util.UUID

case class GameStateModel(me: PlayerModel,
                          players: List[PlayerModel],
                          bullets: List[BulletModel],
                          bonuses: List[BonusModel],
                          time: Long
                         )

case class PlayerModel(id: UUID,
                       name: String,
                       x: Float,
                       y: Float,
                       angle: Float,
                       state: String,
                       bonuses: List[String],
                       frags: Int
                      )

case class BulletModel(id: UUID,
                       x: Float,
                       y: Float,
                       hurting: Boolean
                      )

case class BonusModel(id: UUID,
                      x: Float,
                      y: Float,
                      typeName: String
                     )

