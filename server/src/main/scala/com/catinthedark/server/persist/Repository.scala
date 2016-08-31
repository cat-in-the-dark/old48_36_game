package com.catinthedark.server.persist

import java.sql.ResultSet
import java.util.concurrent.Executors
import java.util.{Date, UUID}

import com.catinthedark.server.models.Player
import com.catinthedark.server.{Config, GeoIPService, IP}
import com.fasterxml.jackson.databind.ObjectMapper
import org.flywaydb.core.Flyway
import org.slf4j.LoggerFactory
import org.sql2o.{ResultSetHandler, Sql2o}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class Repository(private val geoIPService: GeoIPService, private val mapper: ObjectMapper) extends IRepository {
  private val log = LoggerFactory.getLogger(classOf[Repository])
  private implicit val context: ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(4))
  private val sql = new Sql2o(Config.jdbcURL, Config.jdbcUser, Config.jdbcPassword)
  private val flyway = new Flyway()
  flyway.setDataSource(sql.getDataSource)
  flyway.migrate()

  def onPlayerConnect(player: Player) = Future {
    val ip = IP.retrieve(player.socket)
    geoIPService.find(ip) onComplete {
      case Success(geoModel) =>
        val model = PlayerModel(
          uuid = player.socket.getSessionId,
          name = player.entity.name,
          ip = ip,
          geo = geoModel,
          connectedAt = new Date(),
          disconnectedAt = null,
          frags = player.entity.frags,
          deaths = player.entity.deaths)
        createPlayer(model)
        log.info(s"Save $model")
      case Failure(t) => log.error(s"Can't get geo: ${t.getMessage}", t)
    }
  }

  def onPlayerDisconnect(player: Player) = Future {
    updatePlayer(player.socket.getSessionId, new Date(), player.entity.frags, player.entity.deaths)
  }

  override def onRoundFinish(players: List[Player]) = Future {
    val gameModel = GameModel(players = players.map(_.socket.getSessionId), finishedAt = new Date())
    createGame(gameModel)
  }

  val playerResultHandler = new ResultSetHandler[PlayerModel] {
    override def handle(resultSet: ResultSet): PlayerModel = {
      val meta = resultSet.getString("meta")
      try {
        mapper.readValue(meta, classOf[PlayerModel])
      } catch {
        case e: Exception =>
          log.error(s"Can't parse player model: ${e.getMessage}", e)
          null
      }
    }
  }

  private def createPlayer(playerModel: PlayerModel): Unit = {
    val conn = sql.beginTransaction()
    try {
      val meta = mapper.writeValueAsString(playerModel)
      conn.createQuery("INSERT INTO player(id, meta) VALUES(:id, :meta::jsonb)")
        .addParameter("id", playerModel.uuid)
        .addParameter("meta", meta)
        .executeUpdate()
      conn.commit(true)
    } catch {
      case e: Exception =>
        log.error(s"Can't create player ${e.getMessage}", e)
        conn.rollback(true)
    }
  }

  private def updatePlayer(getSessionId: UUID, disconnectedAt: Date, frags: Int, deaths: Int): Unit = {
    val conn = sql.beginTransaction()
    try {
      val oldPlayer = conn.createQuery("SELECT meta FROM player WHERE id = :id")
        .addParameter("id", getSessionId)
        .executeAndFetchFirst(playerResultHandler)
      val newPlayer = oldPlayer.copy(disconnectedAt = disconnectedAt, frags = frags, deaths = deaths)
      conn.createQuery("UPDATE player SET meta = :meta::jsonb WHERE id = :id")
        .addParameter("id", oldPlayer.uuid)
        .addParameter("meta", mapper.writeValueAsString(newPlayer))
        .executeUpdate()
      conn.commit(true)
    } catch {
      case e: Exception =>
        log.error(s"Can't update player ${e.getMessage}", e)
        conn.rollback(true)
    }
  }

  private def createGame(gameModel: GameModel): Unit ={
    val conn = sql.beginTransaction()
    try {
      conn.createQuery("INSERT INTO game(meta) VALUES(:meta::jsonb)")
        .addParameter("meta", mapper.writeValueAsString(gameModel))
        .executeUpdate()
      conn.commit(true)
    } catch {
      case e: Exception =>
        log.error(s"Can't create game ${e.getMessage}", e)
        conn.rollback(true)
    }
  }
}
