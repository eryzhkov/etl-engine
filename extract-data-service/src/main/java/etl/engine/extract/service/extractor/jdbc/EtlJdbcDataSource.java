package etl.engine.extract.service.extractor.jdbc;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class EtlJdbcDataSource implements AutoCloseable {

    private final HikariDataSource dataSource;

    public EtlJdbcDataSource(String databaseType, String host, int port, String database, String user, String password) {
        String jdbcUrl = "jdbc:" + databaseType + "://" + host + ":" + port + "/" + database;
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(user);
        config.setPassword(password);
        config.addDataSourceProperty("maximumPoolSize", 1);
        config.addDataSourceProperty("minimumIdle", 1);
        this.dataSource = new HikariDataSource(config);
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    @Override
    public void close() throws Exception {
        if (dataSource != null) {
            dataSource.close();
        }
    }
}
