package config;

import crawler.CrawlerFactory;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import model.Filter;
import model.Selector;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.DataBaseService;
import service.MysqlDataBaseService;
import util.JsonParser;
import util.PropertiesReader;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class WikiCrawlController {
    private final Logger logger = LoggerFactory.getLogger(WikiCrawlController.class);
    private final PropertiesConfiguration properties;
    private final DataBaseService service;
    private final DataSource dataSource;
    private List<CrawlController> controllers;

    public WikiCrawlController(DataSource dataSource) {
        this.dataSource = dataSource;
        this.service = new MysqlDataBaseService(dataSource);
        this.controllers = new ArrayList<>();
        this.properties = PropertiesReader.getProperties();
    }

    public void start(){
        addController("crawler.namuStorage", "crawler.delayTime", "crawler.namu");
        addController("crawler.wikipediaStorage", "crawler.delayTime", "crawler.wikipedia");
        addController("crawler.rigvedaStorage", "crawler.delayTime", "crawler.rigvedawiki");
        addController("crawler.libreStorage", "crawler.delayTime", "crawler.librewiki");
        addController("crawler.uncycloStorage", "crawler.delayTime", "crawler.uncyclopedia");

        startNonBlocking();
        waitUntilFinish();
    }

    private Selector extractSelector(String seed) {
        String url = properties.getString(seed);
        List<String> result = loadSelector(service);
        for (String value : result) {
            Selector selector = JsonParser.convertObject(value, Selector.class);
            if (url.startsWith(selector.getSiteIdentifier())) {
                return selector;
            }
        }
        return null; //todo optional..
    }

    private Filter extractFilter(String seed) {
        String url = properties.getString(seed);
        List<String> result = loadFilter(service);
        for (String value : result) {
            Filter filter = JsonParser.convertObject(value, Filter.class);
            if(url.startsWith(filter.getSiteIdentifier())){
                return filter;
            }
        }
        return null;
    }

    private List<String> loadSelector(DataBaseService service) {
        return service.findSelector();
    }

    private List<String> loadFilter(DataBaseService service) {
        return service.findFilter();
    }

    private void startNonBlocking() {
        for (CrawlController controller : controllers) {
            controller.startNonBlocking(new CrawlerFactory(dataSource), properties.getInt("crawler.number"));
        }
    }

    private void waitUntilFinish() {
        for (CrawlController controller : controllers) {
            controller.waitUntilFinish();
        }
    }

    private void addController(String stoage, String delayTime, String seed) {
        controllers.add(generateController(stoage, delayTime, seed));
    }

    private RobotstxtServer generateRobotstxtServer(CrawlConfig config) {
        return new RobotstxtServer(new RobotstxtConfig(), new PageFetcher(config));
    }

    private CrawlController generateController(String storage, String delayTime, String seed) {
        CrawlController controller = null;
        try {
            CrawlConfig config = generateConfig(storage, delayTime);
            RobotstxtServer robotServer = generateRobotstxtServer(config);
            controller = new CrawlController(config, new PageFetcher(config), robotServer);
            controller.addSeed(properties.getString(seed));
            controller.setFilter(extractFilter(seed));
            controller.setSelector(extractSelector(seed));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return controller;
    }

    private CrawlConfig generateConfig(String storage, String delayTime) {
        CrawlConfig crawlConfig = new CrawlConfig();
        crawlConfig.setCrawlStorageFolder(properties.getString(storage));
        crawlConfig.setPolitenessDelay(properties.getInt(delayTime));
//        crawlConfig.setMaxDepthOfCrawling(1);
        crawlConfig.setResumableCrawling(true);
        crawlConfig.setShutdownOnEmptyQueue(false);
//        crawlConfig.setMaxPagesToFetch(100);
//        crawlConfig.setMaxOutgoingLinksToFollow();
        return crawlConfig;
    }
}
