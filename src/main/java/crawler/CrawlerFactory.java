package crawler;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import service.DataBaseService;
import service.MysqlDataBaseService;

import javax.sql.DataSource;

public class CrawlerFactory implements CrawlController.WebCrawlerFactory<WikiCrawler> {
    private DataBaseService databaseService;

    public CrawlerFactory(DataBaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public WikiCrawler newInstance() {
        return new WikiCrawler(databaseService);
    }
}
