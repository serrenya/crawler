package edu.uci.ics.crawler4j.crawler;

import edu.uci.ics.crawler4j.crawler.exceptions.ContentFetchException;
import edu.uci.ics.crawler4j.crawler.exceptions.PageBiggerThanMaxSizeException;
import edu.uci.ics.crawler4j.crawler.exceptions.ParseException;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.frontier.DocIDServer;
import edu.uci.ics.crawler4j.frontier.Frontier;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.parser.NotAllowedContentException;
import edu.uci.ics.crawler4j.parser.ParseData;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.WebURL;
import model.Filter;
import model.Selector;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class WebCrawler implements Runnable {
    protected static final Logger logger = LoggerFactory.getLogger(WebCrawler.class);
    protected int myId;
    protected CrawlController myController;
    protected Long startMeasureTime;
    protected Long endMeasureTime;
    private Filter filter;
    private Selector selector;
    private Thread myThread;
    private Parser parser;
    private PageFetcher pageFetcher;
    private RobotstxtServer robotstxtServer;
    private DocIDServer docIdServer;
    private Frontier frontier;
    private boolean isWaitingForNewURLs;

    public WebCrawler() {
    }

    public void init(int id, CrawlController crawlController) throws InstantiationException, IllegalAccessException {
        this.myId = id;
        this.startMeasureTime = 0L;
        this.pageFetcher = crawlController.getPageFetcher();
        this.robotstxtServer = crawlController.getRobotstxtServer();
        this.docIdServer = crawlController.getDocIdServer();
        this.frontier = crawlController.getFrontier();
        this.parser = crawlController.getParser();
        this.myController = crawlController;
        this.isWaitingForNewURLs = false;
        this.selector = crawlController.selector;
        this.filter = crawlController.filter;
    }

    public int getMyId() {
        return this.myId;
    }

    public CrawlController getMyController() {
        return this.myController;
    }

    public void onStart() {
    }

    public void onBeforeExit() {
    }

    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
    }

    protected WebURL handleUrlBeforeProcess(WebURL curURL) {
        return curURL;
    }

    protected void onPageBiggerThanMaxSize(String urlStr, long pageSize) {
        logger.warn("Skipping a URL: {} which was bigger ( {} ) than max allowed size", urlStr, pageSize);
    }

    protected void onRedirectedStatusCode(Page page) {
    }

    protected void onUnexpectedStatusCode(String urlStr, int statusCode, String contentType, String description) {
        logger.warn("Skipping URL: {}, StatusCode: {}, {}, {}", new Object[]{urlStr, statusCode, contentType, description});
    }

    /**
     * @deprecated
     */
    @Deprecated
    protected void onContentFetchError(WebURL webUrl) {
        logger.warn("Can't fetch content of: {}", webUrl.getURL());
    }

    protected void onContentFetchError(Page page) {
        logger.warn("Can't fetch content of: {}", page.getWebURL().getURL());
    }

    protected void onUnhandledException(WebURL webUrl, Throwable e) {
        String urlStr = webUrl == null ? "NULL" : webUrl.getURL();
        logger.warn("Unhandled exception while fetching {}: {}", urlStr, e.getMessage());
        logger.info("Stacktrace: ", e);
    }

    protected void onParseError(WebURL webUrl) {
        logger.warn("Parsing error of: {}", webUrl.getURL());
    }

    public Object getMyLocalData() {
        return null;
    }

    public void run() {
        this.onStart();

        while (true) {
            while (true) {
                List<WebURL> assignedURLs = new ArrayList(50);
                this.isWaitingForNewURLs = true;
                this.frontier.getNextURLs(50, assignedURLs);
                this.isWaitingForNewURLs = false;
                if (assignedURLs.isEmpty()) {
                    if (this.frontier.isFinished()) {
                        return;
                    }

                    try {
                        Thread.sleep(3000L);
                    } catch (InterruptedException var4) {
                        logger.error("Error occurred", var4);
                    }
                } else {
                    Iterator var2 = assignedURLs.iterator();

                    while (var2.hasNext()) {
                        WebURL curURL = (WebURL) var2.next();
                        if (this.myController.isShuttingDown()) {
                            logger.info("Exiting because of controller shutdown.");
                            return;
                        }

                        if (curURL != null) {
                            curURL = this.handleUrlBeforeProcess(curURL);
                            this.processPage(curURL);
                            this.frontier.setProcessed(curURL);
                        }
                    }
                }
            }
        }
    }

    public boolean shouldVisit(Page referringPage, WebURL url) {
        if (!this.myController.getConfig().isRespectNoFollow()) {
            return true;
        } else {
            return (referringPage == null || referringPage.getContentType() == null || !referringPage.getContentType().contains("html") || !((HtmlParseData) referringPage.getParseData()).getMetaTagValue("robots").contains("nofollow")) && !url.getAttribute("rel").contains("nofollow");
        }
    }

    protected boolean shouldFollowLinksIn(WebURL url) {
        return true;
    }

    public void visit(Page page) {
    }

    private void processPage(WebURL curURL) {
        PageFetchResult fetchResult = null;
        Page page = new Page(curURL);

        try {
            try {
                if (curURL == null) {
                    return;
                }

                fetchResult = this.pageFetcher.fetchPage(curURL);
                setStartMeasureTime(System.currentTimeMillis());
                int statusCode = fetchResult.getStatusCode();
                this.handlePageStatusCode(curURL, statusCode, EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode, Locale.ENGLISH));
                page.setFetchResponseHeaders(fetchResult.getResponseHeaders());
                page.setStatusCode(statusCode);
                if (statusCode >= 200 && statusCode <= 299) {
                    if (!curURL.getURL().equals(fetchResult.getFetchedUrl())) {
                        if (this.docIdServer.isSeenBefore(fetchResult.getFetchedUrl())) {
                            logger.debug("Redirect page: {} has already been seen", curURL);
                            return;
                        }

                        curURL.setURL(fetchResult.getFetchedUrl());
                        curURL.setDocid(this.docIdServer.getNewDocID(fetchResult.getFetchedUrl()));
                    }

                    if (!fetchResult.fetchContent(page, this.myController.getConfig().getMaxDownloadSize())) {
                        throw new ContentFetchException();
                    }

                    if (page.isTruncated()) {
                        logger.warn("Warning: unknown page size exceeded max-download-size, truncated to: ({}), at URL: {}", this.myController.getConfig().getMaxDownloadSize(), curURL.getURL());
                    }

                    this.parser.parse(page, curURL.getURL());
                    if (!this.shouldFollowLinksIn(page.getWebURL())) {
                        logger.debug("Not looking for links in page {}, as per your \"shouldFollowLinksInPage\" policy", page.getWebURL().getURL());
                    } else {
                        ParseData parseData = page.getParseData();
                        List<WebURL> toSchedule = new ArrayList();
                        int maxCrawlDepth = this.myController.getConfig().getMaxDepthOfCrawling();
                        Iterator var8 = parseData.getOutgoingUrls().iterator();

                        while (true) {
                            while (var8.hasNext()) {
                                WebURL webURL = (WebURL) var8.next();
                                webURL.setParentDocid(curURL.getDocid());
                                webURL.setParentUrl(curURL.getURL());
                                int newdocid = this.docIdServer.getDocId(webURL.getURL());
                                if (newdocid > 0) {
                                    webURL.setDepth((short) -1);
                                    webURL.setDocid(newdocid);
                                } else {
                                    webURL.setDocid(-1);
                                    webURL.setDepth((short) (curURL.getDepth() + 1));
                                    if (maxCrawlDepth == -1 || curURL.getDepth() < maxCrawlDepth) {
                                        if (this.shouldVisit(page, webURL)) {
                                            if (this.robotstxtServer.allows(webURL)) {
                                                webURL.setDocid(this.docIdServer.getNewDocID(webURL.getURL()));
                                                toSchedule.add(webURL);
                                            } else {
                                                logger.debug("Not visiting: {} as per the server's \"robots.txt\" policy", webURL.getURL());
                                            }
                                        } else {
                                            logger.debug("Not visiting: {} as per your \"shouldVisit\" policy", webURL.getURL());
                                        }
                                    }
                                }
                            }

                            this.frontier.scheduleAll(toSchedule);
                            break;
                        }
                    }

                    boolean noIndex = this.myController.getConfig().isRespectNoIndex() && page.getContentType() != null && page.getContentType().contains("html") && ((HtmlParseData) page.getParseData()).getMetaTagValue("robots").contains("noindex");
                    if (!noIndex) {
                        this.visit(page);
                    }

                    return;
                }

                String movedToUrl;
                if (statusCode != 301 && statusCode != 302 && statusCode != 300 && statusCode != 303 && statusCode != 307 && statusCode != 308) {
                    movedToUrl = EnglishReasonPhraseCatalog.INSTANCE.getReason(fetchResult.getStatusCode(), Locale.ENGLISH);
                    String contentType = fetchResult.getEntity() == null ? "" : (fetchResult.getEntity().getContentType() == null ? "" : fetchResult.getEntity().getContentType().getValue());
                    this.onUnexpectedStatusCode(curURL.getURL(), fetchResult.getStatusCode(), contentType, movedToUrl);
                    return;
                }

                page.setRedirect(true);
                movedToUrl = fetchResult.getMovedToUrl();
                if (movedToUrl == null) {
                    logger.warn("Unexpected error, URL: {} is redirected to NOTHING", curURL);
                    return;
                }

                page.setRedirectedToUrl(movedToUrl);
                this.onRedirectedStatusCode(page);
                if (!this.myController.getConfig().isFollowRedirects()) {
                    return;
                }

                int newDocId = this.docIdServer.getDocId(movedToUrl);
                if (newDocId > 0) {
                    logger.debug("Redirect page: {} is already seen", curURL);
                    return;
                }

                WebURL webURL = new WebURL();
                webURL.setURL(movedToUrl);
                webURL.setParentDocid(curURL.getParentDocid());
                webURL.setParentUrl(curURL.getParentUrl());
                webURL.setDepth(curURL.getDepth());
                webURL.setDocid(-1);
                webURL.setAnchor(curURL.getAnchor());
                if (this.shouldVisit(page, webURL)) {
                    if (this.shouldFollowLinksIn(webURL) && !this.robotstxtServer.allows(webURL)) {
                        logger.debug("Not visiting: {} as per the server's \"robots.txt\" policy", webURL.getURL());
                    } else {
                        webURL.setDocid(this.docIdServer.getNewDocID(movedToUrl));
                        this.frontier.schedule(webURL);
                    }

                    return;
                } else {
                    logger.debug("Not visiting: {} as per your \"shouldVisit\" policy", webURL.getURL());
                    return;
                }
            } catch (PageBiggerThanMaxSizeException var18) {
                this.onPageBiggerThanMaxSize(curURL.getURL(), var18.getPageSize());
            } catch (ParseException var19) {
                this.onParseError(curURL);
            } catch (SocketTimeoutException | ContentFetchException var20) {
                this.onContentFetchError(curURL);
                this.onContentFetchError(page);
            } catch (NotAllowedContentException var21) {
                logger.debug("Skipping: {} as it contains binary content which you configured not to crawl", curURL.getURL());
            } catch (Exception var22) {
                this.onUnhandledException(curURL, var22);
            }

        } finally {
            if (fetchResult != null) {
                fetchResult.discardContentIfNotConsumed();
            }

        }
    }

    public Filter getFilter() {
        return filter;
    }

    public Selector getSelector() {
        return selector;
    }

    public Thread getThread() {
        return this.myThread;
    }

    public void setThread(Thread myThread) {
        this.myThread = myThread;
    }

    public Long getEndMeasureTime() {
        return endMeasureTime;
    }

    public void setEndMeasureTime(Long endMeasureTime) {
        this.endMeasureTime = endMeasureTime;
    }

    protected Long getStartMeasureTime() {
        return startMeasureTime;
    }

    protected void setStartMeasureTime(Long startMeasureTime) {
        this.startMeasureTime = startMeasureTime;
    }

    public boolean isNotWaitingForNewURLs() {
        return !this.isWaitingForNewURLs;
    }
}