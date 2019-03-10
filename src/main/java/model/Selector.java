package model;

public class Selector {
    private String host;
    private String title;
    private String body;
    private String innerUrl;
    private String innerUrlLink;
    private String imageUrl;
    private String imageUrlLink;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

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

    @Override
    public String toString() {
        return "Selector{" +
                "  host='" + host + '\'' +
                ", title='" + title + '\'' +
                ", body='" + body + '\'' +
                ", innerUrl='" + innerUrl + '\'' +
                ", innerUrlLink='" + innerUrlLink + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", imageUrlLink='" + imageUrlLink + '\'' +
                '}';
    }
}
