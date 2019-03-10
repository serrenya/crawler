package model;

import java.util.List;

public class Filter {
    private String host;
    private Boolean includeStatus;
    private String categoryPattern;
    private List<String> categoryFilters;
    private List<String> urlFilters;
    private String extensionFilters;

    public String getExtensionFilters() {
        return extensionFilters;
    }

    public void setExtensionFilters(String extensionFilters) {
        this.extensionFilters = extensionFilters;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Boolean getIncludeStatus() {
        return includeStatus;
    }

    public void setIncludeStatus(Boolean includeStatus) {
        this.includeStatus = includeStatus;
    }

    public String getCategoryPattern() {
        return categoryPattern;
    }

    public void setCategoryPattern(String categoryPattern) {
        this.categoryPattern = categoryPattern;
    }

    public List<String> getCategoryFilters() {
        return categoryFilters;
    }

    public void setCategoryFilters(List<String> categoryFilters) {
        this.categoryFilters = categoryFilters;
    }

    public List<String> getUrlFilters() {
        return urlFilters;
    }

    public void setUrlFilters(List<String> urlFilters) {
        this.urlFilters = urlFilters;
    }
}
