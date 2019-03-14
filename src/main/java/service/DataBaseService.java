package service;

import model.CrawlStatistics;
import model.WikiPage;

import java.sql.ResultSet;
import java.util.List;

public interface DataBaseService {
    void create(WikiPage wikiPage);
    List<String> findSelector();
    List<String> findFilter();
    void create(CrawlStatistics statistics);
}
