package com.catinthedark.models

import java.util.UUID

case class GameStateModel(me: PlayerModel,
                          players: List[PlayerModel],
                          bullets: List[BulletModel],
                          bonuses: List[BonusModel],
                          time: Long
                         )

case class PlayerModel(id: UUID,
                       name: String,
                       var x: Float,
                       var y: Float,
                       var angle: Float,
                       var state: String,
                       var bonuses: List[String],
                       var frags: Int,
                       var deaths: Int,
                       hasBrick: Boolean
                      )

case class BulletModel(id: UUID,
                       x: Float,
                       y: Float,
                       angle: Float,
                       hurting: Boolean
                      )

case class BonusModel(id: UUID,
                      x: Float,
                      y: Float,
                      typeName: String
                     )
