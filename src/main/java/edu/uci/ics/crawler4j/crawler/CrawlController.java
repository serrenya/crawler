package edu.uci.ics.crawler4j.crawler;

import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.frontier.DocIDServer;
import edu.uci.ics.crawler4j.frontier.Frontier;
import edu.uci.ics.crawler4j.parser.Parser;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import edu.uci.ics.crawler4j.url.TLDList;
import edu.uci.ics.crawler4j.url.URLCanonicalizer;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.crawler4j.util.IO;
import model.Filter;
import model.Selector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CrawlController {
    static final Logger logger = LoggerFactory.getLogger(CrawlController.class);
    private final CrawlConfig config;
    protected Object customData;
    protected List<Object> crawlersLocalData;
    protected boolean finished;
    protected boolean shuttingDown;
    protected PageFetcher pageFetcher;
    protected RobotstxtServer robotstxtServer;
    protected Frontier frontier;
    protected DocIDServer docIdServer;
    protected final Object waitingLock;
    protected final Environment env;
    protected Parser parser;
    protected Selector selector;
    protected Filter filter;

    public CrawlController(CrawlConfig config, PageFetcher pageFetcher, RobotstxtServer robotstxtServer) throws Exception {
        this(config, pageFetcher, new Parser(config), robotstxtServer);
    }

    public CrawlController(CrawlConfig config, PageFetcher pageFetcher, Parser parser, RobotstxtServer robotstxtServer) throws Exception {
        this.crawlersLocalData = new ArrayList();
        this.waitingLock = new Object();
        config.validate();
        this.config = config;
        File folder = new File(config.getCrawlStorageFolder());
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                throw new Exception("couldn't create the storage folder: " + folder.getAbsolutePath() + " does it already exist ?");
            }
            logger.debug("Created folder: " + folder.getAbsolutePath());
        }

        TLDList.setUseOnline(config.isOnlineTldListUpdate());
        boolean resumable = config.isResumableCrawling();
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        envConfig.setTransactional(resumable);
        envConfig.setLocking(resumable);
        envConfig.setLockTimeout(config.getDbLockTimeout(), TimeUnit.MILLISECONDS);
        File envHome = new File(config.getCrawlStorageFolder() + "/frontier");
        if (!envHome.exists()) {
            if (!envHome.mkdir()) {
                throw new Exception("Failed creating the frontier folder: " + envHome.getAbsolutePath());
            }

            logger.debug("Created folder: " + envHome.getAbsolutePath());
        }

        if (!resumable) {
            IO.deleteFolderContents(envHome);
            logger.info("Deleted contents of: " + envHome + " ( as you have configured resumable crawling to false )");
        }

        this.env = new Environment(envHome, envConfig);
        this.docIdServer = new DocIDServer(this.env, config);
        this.frontier = new Frontier(this.env, config);
        this.pageFetcher = pageFetcher;
        this.parser = parser;
        this.robotstxtServer = robotstxtServer;
        this.finished = false;
        this.shuttingDown = false;
    }

    public Parser getParser() {
        return this.parser;
    }

    public <T extends WebCrawler> void start(Class<T> clazz, int numberOfCrawlers) {
        this.start(new CrawlController.DefaultWebCrawlerFactory(clazz), numberOfCrawlers, true);
    }

    public <T extends WebCrawler> void start(CrawlController.WebCrawlerFactory<T> crawlerFactory, int numberOfCrawlers) {
        this.start(crawlerFactory, numberOfCrawlers, true);
    }

    public <T extends WebCrawler> void startNonBlocking(CrawlController.WebCrawlerFactory<T> crawlerFactory, int numberOfCrawlers) {
        this.start(crawlerFactory, numberOfCrawlers, false);
    }

    public <T extends WebCrawler> void startNonBlocking(Class<T> clazz, int numberOfCrawlers) {
        this.start(new CrawlController.DefaultWebCrawlerFactory(clazz), numberOfCrawlers, false);
    }

    protected <T extends WebCrawler> void start(final CrawlController.WebCrawlerFactory<T> crawlerFactory, int numberOfCrawlers, boolean isBlocking) {
        try {
            this.finished = false;
            this.crawlersLocalData.clear();
            final List<Thread> threads = new ArrayList();
            final List<T> crawlers = new ArrayList();

            for (int i = 1; i <= numberOfCrawlers; ++i) {
                T crawler = crawlerFactory.newInstance();
                Thread thread = new Thread(crawler, "Crawler " + i);
                crawler.setThread(thread);
                crawler.init(i, this);
                thread.start();
                crawlers.add(crawler);
                threads.add(thread);
                logger.info("Crawler {} started", i);
            }

            Thread monitorThread = new Thread(new Runnable() {
                public void run() {
                    try {
                        synchronized (CrawlController.this.waitingLock) {
                            WebCrawler crawler;
                            while (true) {
                                boolean someoneIsWorking;
                                do {
                                    boolean shutOnEmpty;
                                    do {
                                        do {
                                            CrawlController.sleep(CrawlController.this.config.getThreadMonitoringDelaySeconds());
                                            someoneIsWorking = false;

                                            for (int i = 0; i < threads.size(); ++i) {
                                                Thread threadx = (Thread) threads.get(i);
                                                if (!threadx.isAlive()) {
                                                    if (!CrawlController.this.shuttingDown) {
                                                        CrawlController.logger.info("Thread {} was dead, I'll recreate it", i);
                                                        crawler = crawlerFactory.newInstance();
                                                        threadx = new Thread(crawler, "Crawler " + (i + 1));
                                                        threads.remove(i);
                                                        threads.add(i, threadx);
                                                        crawler.setThread(threadx);
                                                        crawler.init(i + 1, CrawlController.this);
                                                        threadx.start();
                                                        crawlers.remove(i);
                                                        crawlers.add(i, (T) crawler);
                                                    }
                                                } else if (((WebCrawler) crawlers.get(i)).isNotWaitingForNewURLs()) {
                                                    someoneIsWorking = true;
                                                }
                                            }

                                            shutOnEmpty = CrawlController.this.config.isShutdownOnEmptyQueue();
                                        } while (someoneIsWorking);
                                    } while (!shutOnEmpty);

                                    CrawlController.logger.info("It looks like no thread is working, waiting for " + CrawlController.this.config.getThreadShutdownDelaySeconds() + " seconds to make sure...");
                                    CrawlController.sleep(CrawlController.this.config.getThreadShutdownDelaySeconds());
                                    someoneIsWorking = false;

                                    for (int ix = 0; ix < threads.size(); ++ix) {
                                        Thread thread = (Thread) threads.get(ix);
                                        if (thread.isAlive() && ((WebCrawler) crawlers.get(ix)).isNotWaitingForNewURLs()) {
                                            someoneIsWorking = true;
                                        }
                                    }
                                } while (someoneIsWorking);

                                if (CrawlController.this.shuttingDown) {
                                    break;
                                }

                                long queueLength = CrawlController.this.frontier.getQueueLength();
                                if (queueLength <= 0L) {
                                    CrawlController.logger.info("No thread is working and no more URLs are in queue waiting for another " + CrawlController.this.config.getThreadShutdownDelaySeconds() + " seconds to make sure...");
                                    CrawlController.sleep(CrawlController.this.config.getThreadShutdownDelaySeconds());
                                    queueLength = CrawlController.this.frontier.getQueueLength();
                                    if (queueLength > 0L) {
                                        continue;
                                    }
                                    break;
                                }
                            }

                            CrawlController.logger.info("All of the crawlers are stopped. Finishing the process...");
                            CrawlController.this.frontier.finish();
                            Iterator var13 = crawlers.iterator();

                            while (var13.hasNext()) {
                                crawler = (WebCrawler) var13.next();
                                crawler.onBeforeExit();
                                CrawlController.this.crawlersLocalData.add(crawler.getMyLocalData());
                            }

                            CrawlController.logger.info("Waiting for " + CrawlController.this.config.getCleanupDelaySeconds() + " seconds before final clean up...");
                            CrawlController.sleep(CrawlController.this.config.getCleanupDelaySeconds());
                            CrawlController.this.frontier.close();
                            CrawlController.this.docIdServer.close();
                            CrawlController.this.pageFetcher.shutDown();
                            CrawlController.this.finished = true;
                            CrawlController.this.waitingLock.notifyAll();
                            CrawlController.this.env.close();
                        }
                    } catch (Exception var8) {
                        CrawlController.logger.error("Unexpected Error", var8);
                    }
                }
            });
            monitorThread.start();
            if (isBlocking) {
                this.waitUntilFinish();
            }
        } catch (Exception var9) {
            logger.error("Error happened", var9);
        }

    }

    public void waitUntilFinish() {
        while (!this.finished) {
            synchronized (this.waitingLock) {
                if (this.finished) {
                    return;
                }

                try {
                    this.waitingLock.wait();
                } catch (InterruptedException var4) {
                    logger.error("Error occurred", var4);
                }
            }
        }

    }

    public List<Object> getCrawlersLocalData() {
        return this.crawlersLocalData;
    }

    protected static void sleep(int seconds) {
        try {
            Thread.sleep((long) (seconds * 1000));
        } catch (InterruptedException var2) {
        }

    }

    public void addSeed(String pageUrl) {
        this.addSeed(pageUrl, -1);
    }

    public void addSeed(String pageUrl, int docId) {
        String canonicalUrl = URLCanonicalizer.getCanonicalURL(pageUrl);
        if (canonicalUrl == null) {
            logger.error("Invalid seed URL: {}", pageUrl);
        } else {
            if (docId < 0) {
                docId = this.docIdServer.getDocId(canonicalUrl);
                if (docId > 0) {
                    logger.trace("This URL is already seen.");
                    return;
                }

                docId = this.docIdServer.getNewDocID(canonicalUrl);
            } else {
                try {
                    this.docIdServer.addUrlAndDocId(canonicalUrl, docId);
                } catch (Exception var5) {
                    logger.error("Could not add seed: {}", var5.getMessage());
                }
            }

            WebURL webUrl = new WebURL();
            webUrl.setURL(canonicalUrl);
            webUrl.setDocid(docId);
            webUrl.setDepth((short) 0);
            if (this.robotstxtServer.allows(webUrl)) {
                this.frontier.schedule(webUrl);
            } else {
                logger.warn("Robots.txt does not allow this seed: {}", pageUrl);
            }
        }

    }

    public void addSeenUrl(String url, int docId) {
        String canonicalUrl = URLCanonicalizer.getCanonicalURL(url);
        if (canonicalUrl == null) {
            logger.error("Invalid Url: {} (can't cannonicalize it!)", url);
        } else {
            try {
                this.docIdServer.addUrlAndDocId(canonicalUrl, docId);
            } catch (Exception var5) {
                logger.error("Could not add seen url: {}", var5.getMessage());
            }
        }

    }

    public PageFetcher getPageFetcher() {
        return this.pageFetcher;
    }

    public void setPageFetcher(PageFetcher pageFetcher) {
        this.pageFetcher = pageFetcher;
    }

    public RobotstxtServer getRobotstxtServer() {
        return this.robotstxtServer;
    }

    public void setRobotstxtServer(RobotstxtServer robotstxtServer) {
        this.robotstxtServer = robotstxtServer;
    }

    public Frontier getFrontier() {
        return this.frontier;
    }

    public void setFrontier(Frontier frontier) {
        this.frontier = frontier;
    }

    public DocIDServer getDocIdServer() {
        return this.docIdServer;
    }

    public void setDocIdServer(DocIDServer docIdServer) {
        this.docIdServer = docIdServer;
    }

    public Selector getSelector() {
        return selector;
    }

    public void setSelector(Selector selector) {
        this.selector = selector;
    }

    public Filter getFilter() {
        return filter;
    }

    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public Object getCustomData() {
        return this.customData;
    }

    /**
     * @deprecated
     */
    @Deprecated
    public void setCustomData(Object customData) {
        this.customData = customData;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public boolean isShuttingDown() {
        return this.shuttingDown;
    }

    public void shutdown() {
        logger.info("Shutting down...");
        this.shuttingDown = true;
        this.pageFetcher.shutDown();
        this.frontier.finish();
    }

    public CrawlConfig getConfig() {
        return this.config;
    }

    private static class DefaultWebCrawlerFactory<T extends WebCrawler> implements WebCrawlerFactory<WebCrawler> {
        final Class<T> clazz;

        DefaultWebCrawlerFactory(Class<T> clazz) {
            this.clazz = clazz;
        }

        public WebCrawler newInstance() throws Exception {
            try {
                return (WebCrawler) this.clazz.newInstance();
            } catch (ReflectiveOperationException var2) {
                throw var2;
            }
        }
    }

    public interface WebCrawlerFactory<T extends WebCrawler> {
        T newInstance() throws Exception;
    }
}
