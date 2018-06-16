import com.google.inject.Guice.createInjector
import modules.{ActorModule, ConfigLoader, TrendsServiceModule, WebServiceModule}
import services.web.WebService


object main {
  def main(args : Array[String]):Unit = {
    val injector = createInjector(
      new ConfigLoader,
      new WebServiceModule,
      new ActorModule,
      new TrendsServiceModule)

    val webService = injector.getInstance(classOf[WebService])
    webService.startApplication
  }
}