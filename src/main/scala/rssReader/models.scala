package rssReader

import java.util.Date
import play.api.libs.json._

trait BaseItem

final case class XmlRssFeed(title: String, link: String, desc: String, language: String, items: Seq[RssItem]) extends BaseItem

final case class RssItem(title: String, link: String, desc: String, date: Date, guid: String) extends BaseItem

final case class TrendItem(title: String, link: String, desc: String, date: Date, trend: String) extends BaseItem

trait RssJsonSupport {
  implicit val RssItemWriter = new Writes[RssItem] {
    def writes(item: RssItem) = Json.obj(
      "title" -> item.title,
      "link" -> item.link,
      "desc" -> item.desc,
      "date" -> item.date.toString,
      "guid" -> item.guid
    )
  }

  implicit val XmlRssWriter = new Writes[XmlRssFeed] {
    def writes(item: XmlRssFeed) = Json.obj(
      "title" -> item.title,
      "link" -> item.link,
      "desc" -> item.desc,
      "language" -> item.language,
      "items" -> item.items
    )
  }

  implicit val TrendWriter = new Writes[TrendItem] {
    def writes(item: TrendItem) = Json.obj(
      "title" -> item.title,
      "date" -> item.date.toString,
      "link" -> item.link,
      "desc" -> item.desc,
      "trend" -> item.trend
    )
  }
}