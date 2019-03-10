package repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import repository.query.QueryBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ParserRepository {
    private final Logger logger = LogManager.getLogger(ParserRepository.class);
    private static final String SELECTOR = "selector";
    private static final String FILTER = "filter";
    private final DataSource dataSource;

    public ParserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String findSelectorBy(String domain) {
        return executeQuery(domain, SELECTOR);
    }

    public String findFilterBy(String domain) {
        return executeQuery(domain, FILTER);
    }

    private String executeQuery(String domain, String target) {
        String wikiParser = null;
        String query = QueryBuilder.createSelectQueryWithLikeStatement(target);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, "%" + domain + "%");
            ResultSet result = statement.executeQuery();
            while (result.next()) {
                wikiParser = result.getString(target);
            }
        } catch (SQLException e) {
            logger.info("executeQuery() {}" ,e.getMessage());
        }
        return wikiParser;
    }
}
