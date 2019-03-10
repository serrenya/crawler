package model.builder;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import model.Selector;
import model.WikiPage;
import model.handler.SelectorHandler;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import util.TimeGenerator;

import java.io.IOException;

public class WikiPageBuilder {

    public static WikiPage build(Page page, Selector selector) {
        WebURL webURL = page.getWebURL();
        Document document = generateDocumentForJsoup(page);
        SelectorHandler handler = generateSelectorHandler(selector);

        WikiPage wikiPage = generateWikiPage();
        wikiPage.setUrl(webURL.getURL());
        wikiPage.setHost(selector.getHost());
        wikiPage.setTitle(handler.extractTitle(document));
        wikiPage.setContents(handler.extractContents(document));
        wikiPage.setLinks(handler.extractLinks(document));
        wikiPage.setImages(handler.extractImages(document));
        wikiPage.setHtml(handler.extractHtml(document));
        wikiPage.setCrawlTime(TimeGenerator.currentTime());
        wikiPage.setCreateTime(TimeGenerator.currentTime());
        return wikiPage;
    }

    private static Document generateDocumentForJsoup(Page page){
        Document document = null;
        try {
            document = Jsoup.connect(page.getWebURL().getURL()).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return document;
    }

    private static Document generateDocuemnt(Page page) {
        if (!(page.getParseData() instanceof HtmlParseData)) {
           throw new IllegalArgumentException();
        }
        return Jsoup.parse(((HtmlParseData) page.getParseData()).getHtml());
    }

    private static SelectorHandler generateSelectorHandler(Selector selector) {
        return new SelectorHandler(selector);
    }

    private static WikiPage generateWikiPage() {
        return new WikiPage();
    }
}
