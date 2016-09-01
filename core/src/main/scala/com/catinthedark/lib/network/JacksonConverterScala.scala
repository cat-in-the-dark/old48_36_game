package com.catinthedark.lib.network

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.scala.DefaultScalaModule

import scala.collection.mutable

class JacksonConverterScala() extends Converter {
  private val objectMapper = new ObjectMapper()
  objectMapper.registerModule(DefaultScalaModule)
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
  
  override def registerConverter[T](clazz: Class[T], converter: Map[String, Any] => Message): JacksonConverterScala = {
    converters.put(clazz.getCanonicalName, converter)
    this
  }

  override def registerMessage[T <: Message](clazz: Class[T]): JacksonConverterScala = {
    registerConverter(clazz, (data) => {
      defaultConverter(data, clazz)
    })
  }

  private def defaultConverter[T <: Message](data: Map[String, Any], clazz: Class[T]): Message = {
    objectMapper.convertValue(data, clazz)
  }
  
  def registeredConverters = converters.keySet
}
