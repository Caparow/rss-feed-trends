package services.trends

trait TrendsService {
  def updateFeed: Unit

  def getFeed: String
}
