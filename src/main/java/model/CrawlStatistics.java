package model;

import java.sql.Timestamp;

public class CrawlStatistics {
    private Timestamp startTime;
    private Timestamp endTime;
    private String siteIdentifier;
    private String url;
    private Double performanceTime;
    private Long queueLength;

    public Timestamp getStartTime() {
        return startTime;
    }

    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    public String getSiteIdentifier() {
        return siteIdentifier;
    }

    public void setSiteIdentifier(String siteIdentifier) {
        this.siteIdentifier = siteIdentifier;
    }

    public Double getPerformanceTime() {
        return performanceTime;
    }

    public void setPerformanceTime(Double performanceTime) {
        this.performanceTime = performanceTime;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getQueueLength() {
        return queueLength;
    }

    public void setQueueLength(Long queueLength) {
        this.queueLength = queueLength;
    }
}

