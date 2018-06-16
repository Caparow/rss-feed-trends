package rssReader

import akka.actor.Actor
import akka.event.Logging

abstract class BaseActor extends Actor {
  val log = Logging(context.system, this)

  override def postStop(): Unit = {
    log.debug(s"${context.self} stopped")
  }

  override def preStart(): Unit = {
    log.debug(s"${context.self} started")
  }
}