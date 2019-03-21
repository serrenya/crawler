package model.handler;

import model.FieldFilter;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Objects;

public class SelectorHandler {
    private static final String WHITE_SPACE = " ";
    private final FieldFilter fieldFilter;

    public SelectorHandler(FieldFilter fieldFilter) {
        if (Objects.isNull(fieldFilter)) {
            throw new NullPointerException("fieldFilter");
        }
        this.fieldFilter = fieldFilter;
    }

    public String extractTitle(Document document) {
        return document.select(fieldFilter.getTitle()).text();
    }

    public String extractContents(Document document) {
        return extractBody(document).text();
    }

    public String extractHtml(Document document) {
        return extractBody(document).html();
    }

    private Elements extractBody(Document document) {
        return document.select(fieldFilter.getBody());
    }

    public String extractLinks(Document document) {
        Elements links = extractBody(document).select(fieldFilter.getInnerUrl());
        StringBuffer linkBuffer = new StringBuffer();
        for (Element link : links) {
            linkBuffer.append(link.attr(fieldFilter.getInnerUrlLink()) + WHITE_SPACE);
        }
        return linkBuffer.toString();
    }

    public String extractImages(Document document) {
        Elements links = extractBody(document).select(fieldFilter.getImageUrl());
        StringBuffer linkBuffer = new StringBuffer();
        for (Element link : links) {
            linkBuffer.append(link.attr(fieldFilter.getImageUrlLink()) + WHITE_SPACE);
        }
        return linkBuffer.toString();
    }
}
