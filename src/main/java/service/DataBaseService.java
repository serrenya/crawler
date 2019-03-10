package service;

import model.WikiPage;

import java.sql.ResultSet;

public interface DataBaseService {
    void create(WikiPage wikiPage);
    String findSelectorBy(String domain);
    String findFilterBy(String domain);
}
