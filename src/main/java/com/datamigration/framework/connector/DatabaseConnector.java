package com.datamigration.framework.connector;

import com.datamigration.framework.config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Properties;

/**
 * Generic database connector for JDBC-based databases.
 * Supports multiple database types through JDBC drivers.
 */
public class DatabaseConnector implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnector.class);
    
    public enum DatabaseType {
        SOURCE, TARGET
    }
    
    private final ConfigurationManager config;
    private final DatabaseType databaseType;
    private Connection connection;
    private final String connectionUrl;

    public DatabaseConnector(ConfigurationManager config) {
        this(config, DatabaseType.SOURCE);
    }

    public DatabaseConnector(ConfigurationManager config, DatabaseType databaseType) {
        this.config = config;
        this.databaseType = databaseType;
        this.connectionUrl = buildConnectionUrl();
    }

    private String buildConnectionUrl() {
        ConfigurationManager.DatabaseConfig dbConfig = getDatabaseConfig();
        String driver = dbConfig.getDriver() != null ? dbConfig.getDriver() : "sqlserver";
        
        switch (driver.toLowerCase()) {
            case "sqlserver":
            case "mssql":
                return String.format("jdbc:sqlserver://%s:%d;databaseName=%s;encrypt=true;trustServerCertificate=true",
                        dbConfig.getHost(),
                        dbConfig.getPort(),
                        dbConfig.getDatabase());
            case "postgresql":
            case "postgres":
                return String.format("jdbc:postgresql://%s:%d/%s",
                        dbConfig.getHost(),
                        dbConfig.getPort(),
                        dbConfig.getDatabase());
            default:
                logger.warn("Unknown driver type: {}, using SQL Server format", driver);
                return String.format("jdbc:sqlserver://%s:%d;databaseName=%s;encrypt=true;trustServerCertificate=true",
                        dbConfig.getHost(),
                        dbConfig.getPort(),
                        dbConfig.getDatabase());
        }
    }

    private ConfigurationManager.DatabaseConfig getDatabaseConfig() {
        switch (databaseType) {
            case SOURCE:
                return config.getSourceDatabaseConfig();
            case TARGET:
                return config.getTargetDatabaseConfig();
            default:
                return config.getSourceDatabaseConfig();
        }
    }

    /**
     * Establishes a connection to the database.
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            logger.debug("Establishing {} database connection to: {}", databaseType, connectionUrl);
            
            ConfigurationManager.DatabaseConfig dbConfig = getDatabaseConfig();
            Properties props = new Properties();
            props.setProperty("user", dbConfig.getUsername());
            props.setProperty("password", dbConfig.getPassword());
            props.setProperty("loginTimeout", String.valueOf(dbConfig.getConnectionTimeout() != null ? dbConfig.getConnectionTimeout() : 30));
            props.setProperty("socketTimeout", "300000");
            props.setProperty("queryTimeout", "300000");
            
            connection = DriverManager.getConnection(connectionUrl, props);
            connection.setAutoCommit(true);
            logger.info("{} database connection established successfully", databaseType);
        }
        return connection;
    }

    /**
     * Tests the database connection.
     */
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("SELECT 1");
                logger.info("{} database connection test successful", databaseType);
                return true;
            }
        } catch (SQLException e) {
            logger.error("{} database connection test failed: {}", databaseType, e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the current connection is valid and reconnects if necessary.
     */
    public Connection getValidConnection() throws SQLException {
        try {
            if (connection != null && !connection.isClosed()) {
                try (Statement stmt = connection.createStatement()) {
                    stmt.execute("SELECT 1");
                    return connection;
                } catch (SQLException e) {
                    logger.warn("{} database connection is invalid, reconnecting...", databaseType);
                    closeConnection();
                }
            }
        } catch (SQLException e) {
            logger.warn("{} database connection check failed, reconnecting...", databaseType);
            closeConnection();
        }
        
        return getConnection();
    }

    /**
     * Executes a query and returns the ResultSet.
     */
    public ResultSet executeQuery(String sql) throws SQLException {
        logger.debug("Executing query: {}", sql);
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }

    /**
     * Executes a query with parameters and returns the ResultSet.
     */
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        logger.debug("Executing parameterized query: {}", sql);
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        
        return stmt.executeQuery();
    }

    /**
     * Executes an update statement and returns the number of affected rows.
     */
    public int executeUpdate(String sql) throws SQLException {
        logger.debug("Executing update: {}", sql);
        Connection conn = getConnection();
        Statement stmt = conn.createStatement();
        return stmt.executeUpdate(sql);
    }

    /**
     * Executes an update statement with parameters and returns the number of affected rows.
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        logger.debug("Executing parameterized update: {}", sql);
        Connection conn = getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        
        return stmt.executeUpdate();
    }

    /**
     * Closes the database connection.
     */
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                logger.info("Database connection closed");
            } catch (SQLException e) {
                logger.error("Error closing database connection: {}", e.getMessage());
            }
        }
    }

    /**
     * Gets database metadata.
     */
    public DatabaseMetaData getMetaData() throws SQLException {
        return getConnection().getMetaData();
    }

    /**
     * Checks if a table exists.
     */
    public boolean tableExists(String tableName) throws SQLException {
        try (ResultSet rs = getMetaData().getTables(null, null, tableName, new String[]{"TABLE"})) {
            return rs.next();
        }
    }

    /**
     * Gets the list of all tables in the database.
     */
    public ResultSet getAllTables() throws SQLException {
        return getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
    }

    /**
     * Gets the list of all columns for a specific table.
     */
    public ResultSet getTableColumns(String tableName) throws SQLException {
        return getMetaData().getColumns(null, null, tableName, "%");
    }

    /**
     * Gets the list of all indexes for a specific table.
     */
    public ResultSet getTableIndexes(String tableName) throws SQLException {
        return getMetaData().getIndexInfo(null, null, tableName, false, true);
    }

    /**
     * Gets the list of all foreign keys for a specific table.
     */
    public ResultSet getTableForeignKeys(String tableName) throws SQLException {
        return getMetaData().getImportedKeys(null, null, tableName);
    }

    /**
     * Gets the list of all primary keys for a specific table.
     */
    public ResultSet getTablePrimaryKeys(String tableName) throws SQLException {
        return getMetaData().getPrimaryKeys(null, null, tableName);
    }

    /**
     * Gets the row count for a specific table.
     */
    public long getTableRowCount(String tableName) throws SQLException {
        String sql = "SELECT COUNT(*) FROM " + escapeTableName(tableName);
        try (ResultSet rs = executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        }
        return 0;
    }

    /**
     * Escapes a table name for SQL by wrapping it appropriately.
     */
    private String escapeTableName(String tableName) {
        if (tableName == null || tableName.trim().isEmpty()) {
            return tableName;
        }
        // Default to SQL Server format, can be overridden for other databases
        return "[" + tableName.trim() + "]";
    }

    /**
     * AutoCloseable implementation for resource management.
     */
    public void close() {
        closeConnection();
    }

    /**
     * Gets the database type this connector is configured for.
     */
    public DatabaseType getDatabaseType() {
        return databaseType;
    }

    /**
     * Gets the database name this connector is connected to.
     */
    public String getDatabaseName() {
        return getDatabaseConfig().getDatabase();
    }

    /**
     * Gets the database host this connector is connected to.
     */
    public String getDatabaseHost() {
        return getDatabaseConfig().getHost();
    }
}

