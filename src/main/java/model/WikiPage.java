package model;

import java.sql.Timestamp;

public class WikiPage {
    private String url;
    private String host;
    private String title;
    private String contents;
    private String images;
    private String links;
    private String html;
    private Timestamp crawlTime;
    private Timestamp createTime;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLinks() {
        return links;
    }

    public void setLinks(String links) {
        this.links = links;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public void setCrawlTime(Timestamp crawlTime) {
        this.crawlTime = crawlTime;
    }

    public Timestamp getCrawlTime() {
        return crawlTime;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "WikiPage{" +
                "url='" + url + '\'' +
                ", host='" + host + '\'' +
                ", title='" + title + '\'' +
                ", contents='" + contents + '\'' +
                ", images='" + images + '\'' +
                ", links='" + links + '\'' +
                ", html='" + html + '\'' +
                ", crawlTime=" + crawlTime +
                ", createTime=" + createTime +
                '}';
    }
}
