package modules

import com.google.inject.AbstractModule
import services.trends.{TrendsService, TrendsServiceImpl}

class TrendsServiceModule extends AbstractModule {
  override def configure(): Unit = {
    bind(classOf[TrendsService]).to(classOf[TrendsServiceImpl])
  }
}