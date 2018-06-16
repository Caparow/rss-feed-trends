# rss-feed-trends

RSS aggregator that filters news using Google Trends.

To start application run following comands inside of root dir:
```bash
sbt docker:publishLocal
docker run -dit -p 8080:8080 --name rss-feed-trends rss-feed-trends-docker:1.0
```
