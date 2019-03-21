package crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import model.CrawlStatistics;
import model.UrlFilter;
import model.WikiPage;
import model.builder.WikiPageBuilder;
import model.handler.FilterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.DataBaseService;
import util.TimeGenerator;

import java.util.Objects;

public class WikiCrawler extends WebCrawler {

    private static final Double DIVIDE = 1000.0;
    private final Logger logger = LoggerFactory.getLogger(WikiCrawler.class);
    private final DataBaseService dataBaseService;

    public WikiCrawler(DataBaseService dataBaseService) {
        this.dataBaseService = dataBaseService;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        UrlFilter urlFilter = getUrlFilter();
        if (Objects.isNull(urlFilter)) {
            return false;
        }
        FilterHandler filterHandler = generatorFilterHandler(urlFilter);
        return filterHandler.shouldVisit(url.getURL());
    }

    @Override
    public void visit(Page page) {
        WikiPage wikiPage = generatorPageBuilder().build(page);
        setEndMeasureTime(System.currentTimeMillis());
        logger.info("perfomenceTime {} {}", page.getWebURL().getURL(), (getEndMeasureTime() - getStartMeasureTime()) / DIVIDE);
        CrawlStatistics statistics = generateCrawlStatistics(wikiPage);
        dataBaseService.create(statistics);
        dataBaseService.create(wikiPage);
    }

    private WikiPageBuilder generatorPageBuilder() {
        return new WikiPageBuilder(getFieldFilter(), getUrlFilter());
    }

    private FilterHandler generatorFilterHandler(UrlFilter urlFilter) {
        return new FilterHandler(urlFilter);
    }

    private CrawlStatistics generateCrawlStatistics(WikiPage page) {
        CrawlStatistics statistics = new CrawlStatistics();
        statistics.setQueueLength(myController.getFrontier().getQueueLength());
        statistics.setSiteIdentifier(page.getSiteIdentifier());
        statistics.setStartTime(TimeGenerator.currentTimeMillis(getStartMeasureTime()));
        statistics.setEndTime(TimeGenerator.currentTimeMillis(getEndMeasureTime()));
        statistics.setUrl(page.getUrl());
        statistics.setPerformanceTime((getEndMeasureTime() - getStartMeasureTime()) / DIVIDE);

        return statistics;
    }

}
