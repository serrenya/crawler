package config;

import crawler.CrawlerFactory;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import model.FieldFilter;
import model.UrlFilter;
import org.apache.commons.configuration2.PropertiesConfiguration;
import service.DataBaseService;
import service.MysqlDataBaseService;
import util.JsonParser;
import util.PropertiesReader;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class WikiCrawlController {
    private static final Boolean RESUMABLE_FLAGE = true;
    private static final Boolean SHUTDOWN_FLAGE = false;
    private final PropertiesConfiguration properties;
    private final DataBaseService dataBaseService;
    private List<CrawlController> controllers;

    public WikiCrawlController(DataSource dataSource) {
        this.dataBaseService = new MysqlDataBaseService(dataSource);
        this.controllers = new ArrayList<>();
        this.properties = PropertiesReader.getProperties();
    }

    public void start() { //대상 사이트 별로 크롤링 시작
        addController("crawler.namuStorage", "crawler.namu");
        addController("crawler.wikipediaStorage", "crawler.wikipedia");
        addController("crawler.rigvedaStorage", "crawler.rigvedawiki");
        addController("crawler.libreStorage", "crawler.librewiki");
        addController("crawler.uncycloStorage", "crawler.uncyclopedia");

        startNonBlocking();
        waitUntilFinish();
    }

    private FieldFilter extractFieldFilter(String seed) { //사이트 별로 사용되는 필터 맵핑
        String url = properties.getString(seed);
        List<String> result = loadFieldFilter(dataBaseService);
        for (String value : result) {
            FieldFilter fieldFilter = JsonParser.convertObject(value, FieldFilter.class);
            if (url.startsWith(fieldFilter.getSiteIdentifier())) {
                return fieldFilter;
            }
        }
        return null;
    }

    private UrlFilter extractUrlFilter(String seed) {
        String url = properties.getString(seed);
        List<String> result = loadUrlFilter(dataBaseService);
        for (String value : result) {
            UrlFilter urlFilter = JsonParser.convertObject(value, UrlFilter.class);
            if (url.startsWith(urlFilter.getSiteIdentifier())) {
                return urlFilter;
            }
        }
        return null;
    }

    private List<String> loadFieldFilter(DataBaseService service) {
        return service.findFieldFilter();
    }

    private List<String> loadUrlFilter(DataBaseService service) {
        return service.findUrlFilter();
    }

    private void startNonBlocking() { //크롤러 관리하는 팩토리 생성하여 시작
        for (CrawlController controller : controllers) {
            controller.startNonBlocking(new CrawlerFactory(dataBaseService), properties.getInt("crawler.number"));
        }
    }

    private void waitUntilFinish() {
        for (CrawlController controller : controllers) {
            controller.waitUntilFinish();
        }
    }

    private void addController(String stoage, String seed) {
        controllers.add(generateController(stoage, seed));
    }

    private RobotstxtServer generateRobotstxtServer(CrawlConfig config) {
        return new RobotstxtServer(new RobotstxtConfig(), new PageFetcher(config));
    }

    private CrawlController generateController(String storage, String seed) {
        try {
            CrawlConfig config = generateConfig(storage, properties.getInt("crawler.delayTime"));
            RobotstxtServer robotServer = generateRobotstxtServer(config);
            CrawlController controller = new CrawlController(config, new PageFetcher(config), robotServer);
            controller.addSeed(properties.getString(seed));
            controller.setUrlFilter(extractUrlFilter(seed));
            controller.setFieldFilter(extractFieldFilter(seed));
            return controller;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private CrawlConfig generateConfig(String storage, Integer delayTime) {
        CrawlConfig crawlConfig = new CrawlConfig();
        crawlConfig.setCrawlStorageFolder(properties.getString(storage));
        crawlConfig.setPolitenessDelay(delayTime);
        crawlConfig.setResumableCrawling(RESUMABLE_FLAGE);
        crawlConfig.setShutdownOnEmptyQueue(SHUTDOWN_FLAGE);
        return crawlConfig;
    }
}
