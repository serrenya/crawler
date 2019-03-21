package model.builder;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import model.FieldFilter;
import model.UrlFilter;
import model.WikiPage;
import model.handler.FilterHandler;
import model.handler.SelectorHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import util.TimeGenerator;

import java.util.Set;

public class WikiPageBuilder {
    private FieldFilter fieldFilter;
    private UrlFilter urlFilter;
    public WikiPageBuilder(FieldFilter fieldFilter, UrlFilter urlFilter) {
        this.fieldFilter = fieldFilter;
        this.urlFilter = urlFilter; //TODO urlFilter 걷어내기..
    }

    public WikiPage build(Page page) {
        WebURL webURL = page.getWebURL();
        Document document = generateDocuemnt(page);
        Set<WebURL> links = page.getParseData().getOutgoingUrls();

        SelectorHandler selectorHandler = generateSelectorHandler(fieldFilter);
        FilterHandler filterHandler = generateFilterHandler(urlFilter);

        WikiPage wikiPage = generateWikiPage();
        wikiPage.setUrl(webURL.getURL());
        wikiPage.setSiteIdentifier(fieldFilter.getSiteIdentifier());
        wikiPage.setTitle(selectorHandler.extractTitle(document));
        wikiPage.setContents(selectorHandler.extractContents(document));
//        wikiPage.setLinks(filterHandler.extractLinks(links));
//        wikiPage.setImages(filterHandler.extractImages(links));
        wikiPage.setLinks(selectorHandler.extractLinks(document));
        wikiPage.setImages(selectorHandler.extractImages(document));
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

    private SelectorHandler generateSelectorHandler(FieldFilter fieldFilter) {
        return new SelectorHandler(fieldFilter);
    }

    private FilterHandler generateFilterHandler(UrlFilter urlFilter) {
        return new FilterHandler(urlFilter);
    }

    private WikiPage generateWikiPage() {
        return new WikiPage();
    }
}
