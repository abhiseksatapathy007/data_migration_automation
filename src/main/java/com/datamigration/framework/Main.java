package com.datamigration.framework;

import com.datamigration.framework.config.ConfigurationManager;
import com.datamigration.framework.connector.DatabaseConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for Data Migration Automation Framework.
 * Provides entry point for data migration validation and analysis.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            ConfigurationManager config = new ConfigurationManager();
            config.loadConfiguration("application.yaml");
            
            // Test connections to both databases
            testDatabaseConnections(config);
            
            logger.info("Data Migration Framework initialized successfully");
            logger.info("Use the framework's analyzer classes to perform migration validation");
            
        } catch (Exception e) {
            logger.error("Application failed: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
    
    /**
     * Tests connections to both source and target databases.
     */
    private static void testDatabaseConnections(ConfigurationManager config) {
        logger.info("Testing database connections...");
        
        // Test source database connection
        if (config.getSourceDatabaseConfig() != null) {
            try (DatabaseConnector sourceConnector = new DatabaseConnector(config, DatabaseConnector.DatabaseType.SOURCE)) {
                boolean sourceConnected = sourceConnector.testConnection();
                logger.info("Source database connection test: {}", sourceConnected ? "SUCCESS" : "FAILED");
                if (sourceConnected) {
                    logger.info("Source Database: {} on {}", 
                        sourceConnector.getDatabaseName(), sourceConnector.getDatabaseHost());
                }
            } catch (Exception e) {
                logger.error("Failed to test source database connection: {}", e.getMessage());
            }
        } else {
            logger.warn("Source database configuration not found");
        }
        
        // Test target database connection
        if (config.getTargetDatabaseConfig() != null) {
            try (DatabaseConnector targetConnector = new DatabaseConnector(config, DatabaseConnector.DatabaseType.TARGET)) {
                boolean targetConnected = targetConnector.testConnection();
                logger.info("Target database connection test: {}", targetConnected ? "SUCCESS" : "FAILED");
                if (targetConnected) {
                    logger.info("Target Database: {} on {}", 
                        targetConnector.getDatabaseName(), targetConnector.getDatabaseHost());
                }
            } catch (Exception e) {
                logger.error("Failed to test target database connection: {}", e.getMessage());
            }
        } else {
            logger.warn("Target database configuration not found");
        }
    }
}

