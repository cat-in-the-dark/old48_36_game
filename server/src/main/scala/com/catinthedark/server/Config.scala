package com.catinthedark.server

import java.net.URLEncoder

object Config {
  val pb = new ProcessBuilder()

  def gameTick: Int = try {
      pb.environment().getOrDefault("GAME_TICK", "20").toInt
    } catch {
      case e: Exception => 20
    }

  def port: Int = try {
      pb.environment().getOrDefault("PORT", "9000").toInt
    } catch {
      case e: Exception => 9000
    }

  def notificationURL(message: String): String = {
    val key = pb.environment.get("TELEGRAM_KEY")
    val chatId = pb.environment.get("TELEGRAM_CHAT_ID")
    val msg = URLEncoder.encode(message, "UTF-8")
    s"https://api.telegram.org/bot$key/sendMessage?chat_id=$chatId&disable_web_page_preview=1&text=$msg"
  }
}