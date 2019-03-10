package crawler;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import service.MysqlDataBaseService;

import javax.sql.DataSource;

public class CrawlerFactory implements CrawlController.WebCrawlerFactory<WikiCrawler> {
    private DataSource dataSource;

    public CrawlerFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public WikiCrawler newInstance() {
        return new WikiCrawler(new MysqlDataBaseService(dataSource));
    }
}
