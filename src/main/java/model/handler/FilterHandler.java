package model.handler;

import edu.uci.ics.crawler4j.url.WebURL;
import model.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterHandler {
    private static final String WHITE_SPACE = " ";
    private final Logger logger = LoggerFactory.getLogger(FilterHandler.class);
    private final Filter filter;

    public FilterHandler(Filter filter) {
        this.filter = filter;
    }

    public Boolean shouldVisit(String url) {
        Boolean isNotHtmlDocumentLink = isHtmlDocumentLink(url);
        Boolean isNotSubPath = !isSubPath(url);
        Boolean isNotMatchPatter = urlPatternFilter(url);
        Boolean isNotMatchCategory = excludeCategories(url);

        if (isNotHtmlDocumentLink || isNotSubPath || isNotMatchPatter || isNotMatchCategory) {
            return false;
        }
        return true;
    }

    public String extractLinks(Set<WebURL> links){
        StringBuffer linkBuffer = new StringBuffer();
        for (WebURL link : links) {
            if(!isImageFile(link)){
                linkBuffer.append( link.getURL()+ WHITE_SPACE);
            }
        }
        return linkBuffer.toString();
    }

    public String extractImages(Set<WebURL> links){
        StringBuffer linkBuffer = new StringBuffer();
        for (WebURL link : links) {
            if(isImageFile(link)){
                linkBuffer.append( link.getURL()+ WHITE_SPACE);
            }
        }
        return linkBuffer.toString();
    }

    private Boolean isImageFile(WebURL link){
        return Pattern.compile(filter.getImageExtension()).matcher(link.getURL()).matches();
    }

    private Boolean isHtmlDocumentLink(String url) {
        return Pattern.compile(filter.getExtensionFilters()).matcher(url).matches();
    }

    private Boolean isSubPath(String url) {
        return url.startsWith(filter.getSiteIdentifier());
    }

    private Boolean urlPatternFilter(String url) {
        List<String> patterns = filter.getUrlFilters();
        return patterns.stream()
                .anyMatch(pattern -> Pattern
                        .compile(pattern)
                        .matcher(url).matches());
    }

    private Boolean excludeCategories(String url) {
        Boolean isIncludeCategories = filter.getIncludeStatus();
        if (!isIncludeCategories) {
            return false;
        }
        List<String> categories = filter.getCategoryFilters();
        String decodingUrl = extractCategory(decodingUrl(url));
        return categories.stream()
                .anyMatch(category -> category.equals(decodingUrl));
    }

    private String extractCategory(String url) {
        Matcher matcher = Pattern.compile(filter.getCategoryPattern()).matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private String decodingUrl(String url) {
        String result = null;
        try {
            result = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }
}
