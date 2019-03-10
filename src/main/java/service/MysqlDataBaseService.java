package service;

import model.WikiPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.ParserRepository;
import repository.WikiPageReporitory;

import javax.sql.DataSource;

public class MysqlDataBaseService implements DataBaseService {

    private Logger logger = LogManager.getLogger(MysqlDataBaseService.class);
    private final DataSource dataSource;

    public MysqlDataBaseService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public String findSelectorBy(String domain) {
        ParserRepository parserRepository = new ParserRepository(dataSource);
        return parserRepository.findSelectorBy(domain);
    }

    @Override
    public String findFilterBy(String domain) {
        ParserRepository parserRepository = new ParserRepository(dataSource);
        return parserRepository.findFilterBy(domain);
    }

    @Override
    public void create(WikiPage wikiPage) {
        WikiPageReporitory wikiPageReporitory = new WikiPageReporitory(dataSource);
        wikiPageReporitory.create(wikiPage);
    }
}
