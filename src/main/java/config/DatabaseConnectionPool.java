package config;

import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.dbcp2.*;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import util.PropertiesReader;

import javax.sql.DataSource;

public class DatabaseConnectionPool {
    public static DataSource getDataSource() {
        PropertiesConfiguration properties = PropertiesReader.getProperties();
        ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(
                properties.getString("database.url", "jdbc:mysql://localhost:3306/pilot"),
                properties.getString("database.user"),
                properties.getString("database.password"));
        PoolableConnectionFactory poolableConnectionFactory = new PoolableConnectionFactory(connectionFactory, null);


        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMinIdle(properties.getInt("database.minIdle"));
        poolConfig.setMaxTotal(properties.getInt("database.maxTotal"));
        poolConfig.setMaxWaitMillis(properties.getInt("database.maxWait"));

        GenericObjectPool<PoolableConnection> connectionPool = new GenericObjectPool<>(poolableConnectionFactory, poolConfig);
        poolableConnectionFactory.setPool(connectionPool);
        return new PoolingDataSource<>(connectionPool);
    }
}