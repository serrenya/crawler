package crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import model.CrawlStatistics;
import model.Filter;
import model.Selector;
import model.WikiPage;
import model.builder.WikiPageBuilder;
import model.handler.FilterHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.DataBaseService;
import util.TimeGenerator;

import java.util.Objects;

public class WikiCrawler extends WebCrawler {

    private final Logger logger = LoggerFactory.getLogger(WikiCrawler.class);
    private final DataBaseService dataBaseService;

    public WikiCrawler(DataBaseService dataBaseService) {
        this.dataBaseService = dataBaseService;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        Filter filter = getFilter();
        if (Objects.isNull(filter)) {
            return false;
        }
        FilterHandler filterHandler = generatorFilterHandler(filter);
        return filterHandler.shouldVisit(url.getURL());
    }

    @Override
    public void visit(Page page) {
        Selector selector = getSelector();
        WikiPage wikiPage = WikiPageBuilder.build(page,selector,getFilter());
        setEndMeasureTime(System.currentTimeMillis());
        logger.info("perfomenceTime {} {}", page.getWebURL().getURL(), (getEndMeasureTime() - getStartMeasureTime()) / 1000.0);
        logger.info("perfomenceTime - Qlength {}",myController.getFrontier().getQueueLength());

        //Todo q에 들어있는 처리대상 url개수 추가.

        CrawlStatistics statistics = generateCrawlStatistics();
        statistics.setQueueLength(myController.getFrontier().getQueueLength());
        statistics.setSiteIdentifier(wikiPage.getSiteIdentifier()); //siteIdentifier
        statistics.setStartTime(TimeGenerator.currentTimeMillis(getStartMeasureTime()));
        statistics.setEndTime(TimeGenerator.currentTimeMillis(getEndMeasureTime()));
        statistics.setUrl(page.getWebURL().getURL());
        statistics.setPerformanceTime((getEndMeasureTime() - getStartMeasureTime()) / 1000.0);
        dataBaseService.create(statistics);
        dataBaseService.create(wikiPage);
    }

    private FilterHandler generatorFilterHandler(Filter filter){
        return new FilterHandler(filter);
    }

    private CrawlStatistics generateCrawlStatistics() {
        return new CrawlStatistics();
    }
}
