package com.catinthedark.lib.network

import com.catinthedark.models.Message
import com.fasterxml.jackson.databind.ObjectMapper

import scala.collection.mutable

class JacksonConverterScala(val objectMapper: ObjectMapper) extends Converter {
  private val converters = new mutable.HashMap[String, Map[String, Any] => Message]()
  
  override def toJson(data: Message): String = {
    val wrapper = Wrapper(data = data, className = data.getClass.getCanonicalName, sender = null)
    objectMapper.writeValueAsString(wrapper)
  }

  override def fromJson(json: String): Wrapper = {
    val wrapper = objectMapper.readValue(json, classOf[Wrapper])
    val converter = converters.getOrElse(wrapper.className, throw new Exception(s"There is no ${wrapper.className} converter"))
    val data = converter.apply(wrapper.data.asInstanceOf[Map[String, Message]])

    wrapper.copy(data = data)
  }
  
  def registerConverter[T](clazz: Class[T], converter: Map[String, Any] => Message): JacksonConverterScala = {
    converters.put(clazz.getCanonicalName, converter)
    this
  }
  
  def registeredConverters = converters.keySet
}
