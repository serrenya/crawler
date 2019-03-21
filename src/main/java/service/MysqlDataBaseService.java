package service;

import model.CrawlStatistics;
import model.WikiPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.ParserRepository;
import repository.StatisticsRepository;
import repository.WikiPageReporitory;

import javax.sql.DataSource;
import java.util.List;

public class MysqlDataBaseService implements DataBaseService {

    private final DataSource dataSource;

    public MysqlDataBaseService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<String> findFieldFilter() {
        ParserRepository repository = new ParserRepository(dataSource);
        return repository.findFieldFilter();
    }

    @Override
    public List<String> findUrlFilter() {
        ParserRepository parserRepository = new ParserRepository(dataSource);
        return parserRepository.findUrlFilter();
    }

    @Override
    public void create(WikiPage wikiPage) {
        WikiPageReporitory repository = new WikiPageReporitory(dataSource);
        repository.create(wikiPage);
    }

    @Override
    public void create(CrawlStatistics statistics) {
        StatisticsRepository repository = new StatisticsRepository(dataSource);
        repository.create(statistics);
    }


}
