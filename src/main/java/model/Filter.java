package model;

import java.util.List;

public class Filter {
    private String siteIdentifier;
    private String domain;
    private String path;
    private String scheme;
    private Boolean includeStatus;
    private String categoryPattern;
    private List<String> categoryFilters;
    private List<String> urlFilters;
    private String extensionFilters;
    private String imageExtension;

    public String getExtensionFilters() {
        return extensionFilters;
    }

    public void setExtensionFilters(String extensionFilters) {
        this.extensionFilters = extensionFilters;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getSiteIdentifier() {
        return siteIdentifier;
    }

    public void setSiteIdentifier(String siteIdentifier) {
        this.siteIdentifier = siteIdentifier;
    }

    public String getImageExtension() {
        return imageExtension;
    }

    public void setImageExtension(String imageExtension) {
        this.imageExtension = imageExtension;
    }
}
