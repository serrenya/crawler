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

        WikiPage wikiPage = generatorPageBuilder().build(page);
        setEndMeasureTime(System.currentTimeMillis());
        logger.info("perfomenceTime {} {}", page.getWebURL().getURL(), (getEndMeasureTime() - getStartMeasureTime()) / 1000.0); //상수처리
        //TODO 별도의 매소드로 제외시키기
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

    private WikiPageBuilder generatorPageBuilder(){
        return new WikiPageBuilder(getSelector(),getFilter());
    }
    private FilterHandler generatorFilterHandler(Filter filter){
        return new FilterHandler(filter);
    }

    private CrawlStatistics generateCrawlStatistics() {
        return new CrawlStatistics();
    }
}
