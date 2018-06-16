package services.web

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import com.google.inject.{Inject, Singleton}
import config.{RssConfig, WebServiceConfig}
import rssReader.RssJsonSupport
import services.trends.TrendsService
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContextExecutor, Future}
import scala.io.StdIn

@Singleton
class WebServiceImpl @Inject()(webServiceConfig: WebServiceConfig,
                               rssConfig: RssConfig,
                               implicit val system: ActorSystem,
                               trendsService: TrendsService)
  extends WebService
    with RssJsonSupport {

  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  implicit def myRejectionHandler: RejectionHandler =
    RejectionHandler.newBuilder().handleAll[MethodRejection] { methodRejections =>
      val names = methodRejections.map(_.supported.name)
      complete((MethodNotAllowed, s"Can't do that! Supported: ${names mkString " or "}!"))
    }.handleNotFound {
      complete((NotFound, "Not here!"))
    }.result()

  override val route: Route =
    pathPrefix("rss_trends") {
      (get & pathEnd) {
        complete(HttpEntity(ContentTypes.`application/json`, trendsService.getFeed))
      }
    }

  override def startApplication: Unit = {
    val bindingFuture: Future[ServerBinding] = Http().bindAndHandle(route, webServiceConfig.interface, webServiceConfig.port)
    println(s"Server online at http://${webServiceConfig.interface}:${webServiceConfig.port}\nPress RETURN to stop...")
    val updater = system.scheduler.schedule(1.seconds, 30.minutes)(trendsService.updateFeed)
    StdIn.readLine()
    updater.cancel()
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }

}
