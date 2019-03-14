import config.DatabaseConnectionPool;
import config.WikiCrawlController;

public class CrawlerStarter {
    public static void main(String[] args) {
        WikiCrawlController controller = new WikiCrawlController(DatabaseConnectionPool.getDataSource());
        controller.start();
    }
}