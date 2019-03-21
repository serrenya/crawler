package repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.query.QueryBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ParserRepository {
    private static final String FIELD_FILTER = "fieldFilter";
    private static final String URL_FILTER = "urlFilter";
    private final Logger logger = LoggerFactory.getLogger(ParserRepository.class);
    private final DataSource dataSource;

    public ParserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<String> findFieldFilter() {
        return executeQuery(FIELD_FILTER);
    }

    public List<String> findUrlFilter() {
        return executeQuery(URL_FILTER);
    }

    private List<String> executeQuery(String target) {
        List<String> result = new ArrayList<>();
        String query = QueryBuilder.gnerateSelectQuery(target);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.add(resultSet.getString(target));
            }
        } catch (SQLException e) {
            logger.info("executeQuery() {}", e.getMessage());
        }
        return result;
    }
}
