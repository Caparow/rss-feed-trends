package services.trends

import java.io.{BufferedWriter, File, FileWriter}
import java.net.URL

import akka.actor.{ActorRef, ActorSystem, PoisonPill, Props}
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.Inject
import config.RssConfig
import play.api.libs.json.Json
import rssReader._

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContextExecutor, Future}
import scala.io.Source

class TrendsServiceImpl @Inject() (rssConfig: RssConfig,
                                   implicit val system: ActorSystem)
  extends TrendsService
    with RssJsonSupport{

  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  implicit val timeout = Timeout(30.seconds)

  private def checkTitleForTrend(item: RssItem, trends: Seq[String]): TrendItem = {
    TrendItem(
      item.title,
      item.link,
      item.desc,
      item.date,
      trends.filter(item.title.toLowerCase().contains(_)).mkString(";")
    )
  }

  private def writeToFile(res: List[TrendItem]): Unit = {
    val str = Json.prettyPrint(Json.toJson(res))
    val file = new File(rssConfig.saveFile)
    val bw = new BufferedWriter(new FileWriter(file))
    bw.write(str)
    bw.close()
  }

  override def getFeed: String = {
    Source.fromFile(rssConfig.saveFile).getLines().mkString("\n")
  }

  override def updateFeed: Unit = {
    println("started")
    val rssReader: ActorRef = system.actorOf(Props[RssReader])
    val rssFutureList = Future.sequence(
      rssConfig.feedUrl
        .map(u => (rssReader  ? new URL(u)).mapTo[XmlRssFeed])
        .map(_.map(_.items)))
    val trendsFuture = (rssReader ? new URL(rssConfig.trendsUrl)).mapTo[XmlRssFeed]

    val filtered = for {
      rss <- rssFutureList
      trends <- trendsFuture
    } yield {
      val lowerTrends = trends.items.map(_.title.toLowerCase())
      rss.flatten.map(checkTitleForTrend(_, lowerTrends)).filter(_.trend.nonEmpty)
    }
    val res = Await.result(filtered, 3.seconds)
    writeToFile(res)
    rssReader ! PoisonPill
  }
}
