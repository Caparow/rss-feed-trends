package modules

import com.google.inject.AbstractModule
import config.{RssConfig, WebServiceConfig}
import pureconfig._

class ConfigLoader
  extends AbstractModule {

  override def configure(): Unit = {
    val webServiceConfig = loadConfig[WebServiceConfig]("rssFeedTrends.web").right.get
    bind(classOf[WebServiceConfig]).toInstance(webServiceConfig)

    val rssConfig = loadConfig[RssConfig]("rssFeedTrends.rss").right.get
    bind(classOf[RssConfig]).toInstance(rssConfig)
  }
}