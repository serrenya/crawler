package config;

import crawler.CrawlerFactory;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.omg.CORBA.CODESET_INCOMPATIBLE;
import util.PropertiesReader;

import java.util.ArrayList;
import java.util.List;

public class WikiCrawlController { //TODO  중복제거
    static PropertiesConfiguration properties = PropertiesReader.getProperties();

    public static void main(String[] args) throws Exception {
        List<CrawlController> controllers = new ArrayList<>();
        CrawlConfig namuConfig = generateConfig("crawler.namuStorage", "crawler.delayTime");
        CrawlConfig wikiConfig = generateConfig("crawler.wikipediaStorage", "crawler.delayTime");
        CrawlConfig rigvedaConfig = generateConfig("crawler.rigvedaStorage", "crawler.delayTime");
        CrawlConfig libreConfig = generateConfig("crawler.libreStorage", "crawler.delayTime");
        CrawlConfig uncycloConfig = generateConfig("crawler.uncycloStorage", "crawler.delayTime");

        PageFetcher namuFetcher = new PageFetcher(namuConfig);
        PageFetcher wikiFetcher = new PageFetcher(wikiConfig);
        PageFetcher rigvedaFetcher = new PageFetcher(rigvedaConfig);
        PageFetcher libreFetcher = new PageFetcher(libreConfig);
        PageFetcher uncycloFetcher = new PageFetcher(uncycloConfig);

        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, namuFetcher);

        CrawlController namu = new CrawlController(namuConfig, namuFetcher, robotstxtServer);
        CrawlController wikipedia = new CrawlController(wikiConfig, wikiFetcher, robotstxtServer);
        CrawlController regveda = new CrawlController(rigvedaConfig, rigvedaFetcher, robotstxtServer);
        CrawlController libre = new CrawlController(libreConfig, libreFetcher, robotstxtServer);
        CrawlController uncyclo = new CrawlController(uncycloConfig, uncycloFetcher, robotstxtServer);

        namu.addSeed(properties.getString("crawler.namu"));
        wikipedia.addSeed(properties.getString("crawler.wikipedia"));
        regveda.addSeed(properties.getString("crawler.rigvedawiki"));
        libre.addSeed(properties.getString("crawler.librewiki"));
        uncyclo.addSeed(properties.getString("crawler.uncyclopedia"));

        controllers.add(namu);
        controllers.add(wikipedia);
        controllers.add(regveda);
        controllers.add(libre);
        controllers.add(uncyclo);


        for (CrawlController controller : controllers) {
            controller.startNonBlocking(new CrawlerFactory(DatabaseConnectionPool.getDataSource()), properties.getInt("crawler.number"));
        }

//
//        namu.startNonBlocking(new CrawlerFactory(DatabaseConnectionPool.getDataSource()), properties.getInt("crawler.number"));
//        wikipedia.startNonBlocking(new CrawlerFactory(DatabaseConnectionPool.getDataSource()), properties.getInt("crawler.number"));
//        regveda.startNonBlocking(new CrawlerFactory(DatabaseConnectionPool.getDataSource()), properties.getInt("crawler.number"));
//        libre.startNonBlocking(new CrawlerFactory(DatabaseConnectionPool.getDataSource()), properties.getInt("crawler.number"));
//        uncyclo.startNonBlocking(new CrawlerFactory(DatabaseConnectionPool.getDataSource()), properties.getInt("crawler.number"));
//
        namu.waitUntilFinish();
        wikipedia.waitUntilFinish();
        regveda.waitUntilFinish();
        libre.waitUntilFinish();
        uncyclo.waitUntilFinish();
    }

    private static CrawlConfig generateConfig(String storage, String delayTime) {
        CrawlConfig crawlConfig = new CrawlConfig();
        crawlConfig.setCrawlStorageFolder(properties.getString(storage));
        crawlConfig.setPolitenessDelay(properties.getInt(delayTime));
        crawlConfig.setResumableCrawling(true);
        crawlConfig.setShutdownOnEmptyQueue(false);
        return crawlConfig;
    }
}
