package service;

import model.CrawlStatistics;
import model.WikiPage;

import java.sql.ResultSet;
import java.util.List;

public interface DataBaseService {
    void create(WikiPage wikiPage);
    List<String> findFieldFilter();
    List<String> findUrlFilter();
    void create(CrawlStatistics statistics);
}
