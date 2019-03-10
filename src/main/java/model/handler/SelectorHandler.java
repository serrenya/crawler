package model.handler;

import model.Selector;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Objects;

public class SelectorHandler {
    private static final String WHITE_SPACE = " ";
    private final Selector selector;

    public SelectorHandler(Selector selector) {
        if (Objects.isNull(selector)) {
            throw new NullPointerException("selector");
        }
        this.selector = selector;
    }

    public String extractTitle(Document document) {
        return document.select(selector.getTitle()).text();
    }

    public String extractContents(Document document) {
        return extractBody(document).text();
    }

    public String extractHtml(Document document) {
        return extractBody(document).html();
    }

    private Elements extractBody(Document document) {
        return document.select(selector.getBody());
    }

    public String extractLinks(Document document) {
        Elements links = extractBody(document).select(selector.getInnerUrl());
        StringBuffer linkBuffer = new StringBuffer();
        for (Element link : links) {
            linkBuffer.append(link.attr(selector.getInnerUrlLink()) + WHITE_SPACE);
        }
        return linkBuffer.toString();
    }

    public String extractImages(Document document) {
        Elements links = extractBody(document).select(selector.getImageUrl());
        StringBuffer linkBuffer = new StringBuffer();
        for (Element link : links) {
            linkBuffer.append(link.attr(selector.getImageUrlLink()) + WHITE_SPACE);
        }
        return linkBuffer.toString();
    }
}
