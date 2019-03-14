package model.builder;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import model.Filter;
import model.Selector;
import model.WikiPage;
import model.handler.FilterHandler;
import model.handler.SelectorHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import util.TimeGenerator;

import java.util.Set;

public class WikiPageBuilder {

    private Selector selector;
    private Filter filter;

    public WikiPageBuilder(Selector selector, Filter filter) {
        this.selector = selector;
        this.filter = filter;
    }

    public WikiPage build(Page page) {
        WebURL webURL = page.getWebURL();
        Document document = generateDocuemnt(page);
        Set<WebURL> links = page.getParseData().getOutgoingUrls();

        SelectorHandler selectorHandler = generateSelectorHandler(selector);
        FilterHandler filterHandler = generateFilterHandler(filter);

        WikiPage wikiPage = generateWikiPage();
        wikiPage.setUrl(webURL.getURL());
        wikiPage.setSiteIdentifier(selector.getSiteIdentifier());
        wikiPage.setTitle(selectorHandler.extractTitle(document));
        wikiPage.setContents(selectorHandler.extractContents(document));
        wikiPage.setLinks(filterHandler.extractLinks(links));
        wikiPage.setImages(filterHandler.extractImages(links));
        wikiPage.setHtml(selectorHandler.extractHtml(document));
        wikiPage.setCrawlTime(TimeGenerator.currentTime());
        wikiPage.setCreateTime(TimeGenerator.currentTime());
        return wikiPage;
    }

    private Document generateDocuemnt(Page page) {
        if (!(page.getParseData() instanceof HtmlParseData)) {
            throw new IllegalArgumentException();
        }
        return Jsoup.parse(((HtmlParseData) page.getParseData()).getHtml());
    }

    private SelectorHandler generateSelectorHandler(Selector selector) {
        return new SelectorHandler(selector);
    }

    private FilterHandler generateFilterHandler(Filter filter) {
        return new FilterHandler(filter);
    }

    private WikiPage generateWikiPage() {
        return new WikiPage();
    }
}
