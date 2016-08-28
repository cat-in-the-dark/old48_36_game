package com.catinthedark.models

import java.util.UUID

case class GameStateModel(me: PlayerModel,
                          players: List[PlayerModel],
                          bricks: List[BrickModel],
                          bonuses: List[BonusModel],
                          time: Long
                         )

case class PlayerModel(id: UUID,
                       name: String,
                       var x: Float,
                       var y: Float,
                       var oldX: Float,
                       var oldY: Float,
                       var angle: Float,
                       var state: String,
                       var bonuses: scala.collection.mutable.ListBuffer[String],
                       var frags: Int,
                       var deaths: Int,
                       var hasBrick: Boolean
                      )

case class BrickModel(id: UUID,
                      var x: Float,
                      var y: Float,
                      var angle: Float,
                      var hurting: Boolean
                      )

case class BonusModel(id: UUID,
                      x: Float,
                      y: Float,
                      typeName: String
                     )
