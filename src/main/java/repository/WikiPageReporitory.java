package repository;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import model.WikiPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.query.QueryBuilder;
import util.JsonParser;
import util.MD5;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WikiPageReporitory {
    private static final String CRAWL_STATUS = "SUCCESS";
    private final Logger logger = LoggerFactory.getLogger(WikiPageReporitory.class);
    private final DataSource dataSource;

    public WikiPageReporitory(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void create(WikiPage wikiPage) {
        String query = QueryBuilder.createInsertQueryWithUpdate();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, MD5.valueOf(wikiPage.getUrl()));
            statement.setString(2, wikiPage.getSiteIdentifier());
            statement.setString(3, wikiPage.getUrl());
            statement.setString(4, JsonParser.convertJson(wikiPage));
            statement.setString(5, CRAWL_STATUS);
            statement.setTimestamp(6, wikiPage.getCrawlTime());
            statement.setTimestamp(7, wikiPage.getCreateTime());
            statement.executeUpdate();
        } catch (MySQLIntegrityConstraintViolationException e) {
            logger.info("create() {}", e.getMessage());
        } catch (SQLException e) {
            logger.info("create() {} ", e.getMessage());
        }
    }
}
