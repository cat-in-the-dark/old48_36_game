package com.catinthedark.lib

import scala.collection.mutable

/**
  * Created by over on 13.12.14.
  */


class RouteMachine {
  private val routes = mutable.ListBuffer[(YieldUnit[Any, Any], Any => YieldUnit[Any, Any])]()
  private var current: YieldUnit[Any, Any] = _

  def doRoute[T](cond: T): Unit = {
    val from = current
    println(s"begin transition from $from")
    from.onExit()

    val routeFn = routes.filter(route => {
      val (routeFrom, _) = route
      routeFrom eq from //reference equal
    }).map(route => route._2)
      .headOption
      .getOrElse(throw new RuntimeException(s"Could not find route function from $from"))
    val to = routeFn(cond)
    to.onActivate(cond)
    println(s"end transition to $to")
    current = to
  }

  def addRoute[T](from: YieldUnit[_, T], routeFn: T => YieldUnit[T, _]): Unit =
    routes += ((from.asInstanceOf[YieldUnit[Any, Any]], routeFn.asInstanceOf[Any => YieldUnit[Any, Any]]))

  def start(unit: YieldUnit[Any, Any]) = {
    current = unit
    unit.onActivate()
  }

  def run(delta: Float): Unit = {
    val data = current.run(delta)
    data match {
      case Some(res) => doRoute(res)
      case _ =>
    }
  }
}
