package modules

import akka.actor.ActorSystem
import com.google.inject.AbstractModule

class ActorModule extends AbstractModule {
  def configure: Unit = {
    bind(classOf[ActorSystem]).toInstance(ActorSystem("rss-feed-trends"))
  }
}