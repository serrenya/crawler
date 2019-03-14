package repository;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import model.CrawlStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import repository.query.QueryBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StatisticsRepository {
    private final Logger logger = LoggerFactory.getLogger(StatisticsRepository.class);
    private final DataSource dataSource;

    public StatisticsRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void create(CrawlStatistics statistics) {
        String query = QueryBuilder.createInsertQuery();
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setTimestamp(1, statistics.getStartTime());
            statement.setTimestamp(2, statistics.getEndTime());
            statement.setString(3, statistics.getSiteIdentifier());
            statement.setString(4, statistics.getUrl());
            statement.setDouble(5, statistics.getPerformanceTime());
            statement.setLong(6, statistics.getQueueLength());
            statement.executeUpdate();
        } catch (MySQLIntegrityConstraintViolationException e) {
            logger.info("create() {}", e.getMessage());
        } catch (SQLException e) {
            logger.info("create() {} ", e.getMessage());
        }
    }
}
