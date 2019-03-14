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
    private static final String SELECTOR = "selector";
    private static final String FILTER = "filter";
    private final Logger logger = LoggerFactory.getLogger(ParserRepository.class);
    private final DataSource dataSource;

    public ParserRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<String> findSelector() {
        return executeQuery(SELECTOR);
    }

    public List<String> findFilter() {
        return executeQuery(FILTER);
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
