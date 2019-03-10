package crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.url.WebURL;
import model.*;
import model.builder.WikiPageBuilder;
import model.handler.FilterHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.DataBaseService;
import util.JsonParser;

import java.util.Objects;

public class WikiCrawler extends WebCrawler {

    private Logger logger = LogManager.getLogger(WikiCrawler.class);
    private DataBaseService dataBaseService;

    public WikiCrawler(DataBaseService dataBaseService) {
        this.dataBaseService = dataBaseService;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        String filterFromDB = dataBaseService.findFilterBy(url.getDomain());
        if (Objects.isNull(filterFromDB)) {
            return false;
        }
        logger.info("shoud Visit Url {}" , url.getURL());
        logger.info("filter from db {} ",filterFromDB);
        Filter filter = JsonParser.convertObject(filterFromDB,Filter.class);
        FilterHandler filterHandler = generatorFilterHandler(filter);
        return filterHandler.shouldVisit(href);
    }

    @Override
    public void visit(Page page) {
        logger.info("visited Url {}", page.getWebURL().getURL());
        String selectorFromDB = dataBaseService.findSelectorBy(page.getWebURL().getDomain());
        Selector selector = JsonParser.convertObject(selectorFromDB, Selector.class);
        WikiPage wikiPage = WikiPageBuilder.build(page,selector);
        dataBaseService.create(wikiPage);
    }

    @Override
    public void onBeforeExit() {
        logger.info("onBeforeExit() crawl closed.");
    }

    private FilterHandler generatorFilterHandler(Filter filter){
        return new FilterHandler(filter);
    }
}
