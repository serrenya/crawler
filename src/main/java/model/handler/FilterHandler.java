package model.handler;

import model.Filter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FilterHandler {
    private Logger logger = LogManager.getLogger(FilterHandler.class);
    private static final Pattern PICTURE_EXTENTION = Pattern.compile(".*(\\.(|bmp|gif|jpe?g|JPE?G|png))$");
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

    private Boolean isHtmlDocumentLink(String url) {
        return Pattern.compile(filter.getExtensionFilters()).matcher(url).matches();
    }

    private Boolean isSubPath(String url) {
        return url.startsWith(filter.getHost());
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
