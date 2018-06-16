package rssReader

import java.text.SimpleDateFormat
import java.util.{Date, Locale}

import akka.actor.PoisonPill
import akka.pattern.pipe

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.Future
import scala.xml.Elem

class XmlReader extends BaseActor {

  val dateFormatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)

  def extract(xml: Elem): Future[XmlRssFeed] = {
    Future {
      val chan = xml \\ "channel"
      val items = for (item <- chan \\ "item") yield {
        val strDate = (item \\ "pubDate").text
        val date = if (strDate.isEmpty) new Date() else dateFormatter.parse(strDate)
        val desc = (item \\ "description").text
        val index = desc.indexOf("<")
        val eraseDesc = desc.dropRight(if (index != -1) desc.length - index else 0)
        RssItem(
          (item \\ "title").text,
          (item \\ "link").text,
          eraseDesc,
          date,
          (item \\ "guid").text
        )
      }
      XmlRssFeed(
        (chan \ "title").text,
        (chan \ "link").text,
        (chan \ "description").text,
        (chan \ "language").text,
        items)
    }
  }


  def receive() = {
    case xml: Elem =>
      extract(xml) pipeTo sender
      context.system.scheduler.scheduleOnce(5.second, self, PoisonPill)
  }
}