package com.datamigration.framework.connector;

import com.datamigration.framework.config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Generic DynamoDB connector for data migration framework.
 * Supports connecting to source and target DynamoDB instances.
 */
public class DynamoDBConnector implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(DynamoDBConnector.class);
    
    public enum DynamoDBType {
        SOURCE, TARGET
    }
    
    private final ConfigurationManager config;
    private final DynamoDBType dynamoDBType;
    private DynamoDbClient client;

    public DynamoDBConnector(ConfigurationManager config) {
        this(config, DynamoDBType.SOURCE);
    }

    public DynamoDBConnector(ConfigurationManager config, DynamoDBType dynamoDBType) {
        this.config = config;
        this.dynamoDBType = dynamoDBType;
    }

    /**
     * Gets or creates the DynamoDB client.
     */
    public DynamoDbClient getClient() {
        if (client == null) {
            ConfigurationManager.DynamoDBConfig dbConfig = getDynamoDBConfig();
            
            if (dbConfig == null) {
                throw new IllegalStateException("DynamoDB configuration not found for " + dynamoDBType);
            }
            
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                dbConfig.getAccessKey(),
                dbConfig.getSecretKey()
            );
            
            var builder = DynamoDbClient.builder()
                .region(Region.of(dbConfig.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds));
            
            if (dbConfig.getEndpointUrl() != null && !dbConfig.getEndpointUrl().isEmpty()) {
                builder.endpointOverride(URI.create(dbConfig.getEndpointUrl()));
            }
            
            client = builder.build();
            logger.info("DynamoDB client created for {} (region: {})", dynamoDBType, dbConfig.getRegion());
        }
        return client;
    }

    private ConfigurationManager.DynamoDBConfig getDynamoDBConfig() {
        switch (dynamoDBType) {
            case SOURCE:
                return config.getSourceDynamoDBConfig();
            case TARGET:
                return config.getTargetDynamoDBConfig();
            default:
                return config.getSourceDynamoDBConfig();
        }
    }

    /**
     * Tests the DynamoDB connection by listing tables.
     */
    public boolean testConnection() {
        try {
            ListTablesResponse response = getClient().listTables();
            logger.info("DynamoDB connection test successful. Found {} tables", response.tableNames().size());
            return true;
        } catch (Exception e) {
            logger.error("DynamoDB connection test failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Lists all tables in the DynamoDB instance.
     */
    public List<String> listTables() {
        try {
            ListTablesResponse response = getClient().listTables();
            return new ArrayList<>(response.tableNames());
        } catch (Exception e) {
            logger.error("Failed to list DynamoDB tables: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Gets the count of items in a table.
     */
    public long getTableItemCount(String tableName) {
        try {
            ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .select(Select.COUNT)
                .build();
            
            ScanResponse response = getClient().scan(scanRequest);
            return response.count();
        } catch (Exception e) {
            logger.error("Failed to get item count for table {}: {}", tableName, e.getMessage());
            return 0;
        }
    }

    /**
     * Scans a table and returns all items.
     */
    public List<Map<String, AttributeValue>> scanTable(String tableName) {
        List<Map<String, AttributeValue>> items = new ArrayList<>();
        try {
            ScanRequest scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .build();
            
            ScanResponse response = getClient().scan(scanRequest);
            items.addAll(response.items());
            
            // Handle pagination
            while (response.lastEvaluatedKey() != null && !response.lastEvaluatedKey().isEmpty()) {
                scanRequest = ScanRequest.builder()
                    .tableName(tableName)
                    .exclusiveStartKey(response.lastEvaluatedKey())
                    .build();
                response = getClient().scan(scanRequest);
                items.addAll(response.items());
            }
        } catch (Exception e) {
            logger.error("Failed to scan table {}: {}", tableName, e.getMessage());
        }
        return items;
    }

    /**
     * Gets table description.
     */
    public TableDescription describeTable(String tableName) {
        try {
            DescribeTableRequest request = DescribeTableRequest.builder()
                .tableName(tableName)
                .build();
            
            DescribeTableResponse response = getClient().describeTable(request);
            return response.table();
        } catch (Exception e) {
            logger.error("Failed to describe table {}: {}", tableName, e.getMessage());
            return null;
        }
    }

    /**
     * Gets the table prefix from configuration.
     */
    public String getTablePrefix() {
        ConfigurationManager.DynamoDBConfig dbConfig = getDynamoDBConfig();
        return dbConfig != null ? dbConfig.getTablePrefix() : null;
    }

    /**
     * Gets the region from configuration.
     */
    public String getRegion() {
        ConfigurationManager.DynamoDBConfig dbConfig = getDynamoDBConfig();
        return dbConfig != null ? dbConfig.getRegion() : null;
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
            logger.info("DynamoDB client closed");
        }
    }
}

