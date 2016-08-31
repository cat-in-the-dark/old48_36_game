package com.catinthedark.server

import java.net.URL
import java.util.concurrent.Executors

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory

import scala.concurrent.{ExecutionContext, Future}

class GeoIPService(private val mapper: ObjectMapper) {
  private val log = LoggerFactory.getLogger(classOf[GeoIPService])
  private val apiHost = "http://ip-api.com/json/"
  private val executor = Executors.newFixedThreadPool(4)
  private val context = ExecutionContext.fromExecutor(executor)

  def find(ip: String): Future[GeoModel] = Future {
    if (ip == null) return null
    log.info(s"Search geo info for ip: $ip")
    try {
      val model = mapper.readValue(new URL(s"$apiHost$ip"), classOf[GeoModel])
      if (model.status == "success") {
        model
      } else {
        null
      }
    } catch {
      case e: Exception =>
        log.error(s"Can't get geoInfo: ${e.getMessage}", e)
        null
    }
  }(context)
}

case class GeoModel(
                     status: String,
                     message: String,
                     country: String, countryCode: String,
                     region: String, regionName: String,
                     city: String, zip: String,
                     lat: Double, lon: Double,
                     timezone: String,
                     isp: String, org: String, as: String,
                     query: String)