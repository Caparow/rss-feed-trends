package rssReader

import java.net.URL

import akka.actor.Props
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.xml.XML

class RssReader extends BaseActor {
  implicit val timeout = Timeout(30.seconds)

  def read(url : URL): Future[XmlRssFeed] = {
    Future(url.openConnection.getInputStream).flatMap{url =>
      val xml = XML.load(url)
      val actor = context.actorOf(Props[XmlReader])
      ask(actor, xml).mapTo[XmlRssFeed]
    }
  }

  def receive() = {
    case path:URL => read(path) pipeTo sender
  }
}