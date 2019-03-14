package model;

public class Selector {
    private String siteIdentifier;
    private String domain;
    private String path;
    private String scheme;
    private String title;
    private String body;
    private String innerUrl;
    private String innerUrlLink;
    private String imageUrl;
    private String imageUrlLink;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getInnerUrl() {
        return innerUrl;
    }

    public void setInnerUrl(String innerUrl) {
        this.innerUrl = innerUrl;
    }

    public String getInnerUrlLink() {
        return innerUrlLink;
    }

    public void setInnerUrlLink(String innerUrlLink) {
        this.innerUrlLink = innerUrlLink;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageUrlLink() {
        return imageUrlLink;
    }

    public void setImageUrlLink(String imageUrlLink) {
        this.imageUrlLink = imageUrlLink;
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
}
