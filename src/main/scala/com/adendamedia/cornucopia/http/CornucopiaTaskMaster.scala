package com.adendamedia.cornucopia.http

import akka.actor._
import akka.util.Timeout
import com.adendamedia.cornucopia.graph.CornucopiaActorSource
import com.adendamedia.cornucopia.redis.Connection.{Salad, newSaladAPI}

object CornucopiaTaskMaster {
  def props(implicit timeout: Timeout) = Props(new CornucopiaTaskMaster)

  case class RestTask(operation: String, redisNodeIp: String)
}

class CornucopiaTaskMaster(implicit timeout: Timeout) extends Actor with ActorLogging {
  import CornucopiaTaskMaster._
  import com.adendamedia.cornucopia.actors.CornucopiaSource._

  implicit val newSaladAPIimpl: Salad = newSaladAPI
  val ref: ActorRef = new CornucopiaActorSource().ref

  def receive = {
    case RestTask(operation, redisNodeIp) =>
      log.info(s"Received Cornucopia API task request: '$operation', '$redisNodeIp'")
      sender ! Right("its all good")
      ref ! Task(operation, redisNodeIp, Some(self))
    case Right(msg) =>
      log.info(s"Received task completion: $msg")
    case Left(msg) =>
      log.info(s"Received task failed: $msg")
  }
}
