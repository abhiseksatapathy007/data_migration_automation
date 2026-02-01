package com.datamigration.framework.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Manages application configuration loading from YAML files.
 * Supports multiple data sources and extensible configuration.
 */
public class ConfigurationManager {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationManager.class);
    
    private DatabaseConfig sourceDatabaseConfig;
    private DatabaseConfig targetDatabaseConfig;
    private List<DatabaseConfig> additionalDatabaseConfigs;
    private DynamoDBConfig sourceDynamoDBConfig;
    private DynamoDBConfig targetDynamoDBConfig;
    private ReportConfig reportConfig;
    private LoggingConfig loggingConfig;
    private S3Config s3Config;
    private S3Config s3SourceConfig;

    public ConfigurationManager() throws IOException {
        // Try to load from project root first
        String configPath = "application.yaml";
        File projectRootConfig = new File(configPath);
        
        if (projectRootConfig.exists()) {
            loadConfiguration(projectRootConfig.getAbsolutePath());
        } else {
            // Fallback to relative path
            loadConfiguration(configPath);
        }
    }

    public void loadConfiguration(String configPath) throws IOException {
        logger.info("Loading configuration from: {}", configPath);
        
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        
        // First try to load from file system (project root)
        File configFile = new File(configPath);
        if (configFile.exists()) {
            logger.info("Loading configuration from file system: {}", configFile.getAbsolutePath());
            @SuppressWarnings("unchecked")
            Map<String, Object> config = mapper.readValue(configFile, Map.class);
            parseConfiguration(config);
        } else {
            // Fallback to classpath resource
            logger.info("File not found in file system, trying classpath resource: {}", configPath);
            try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(configPath)) {
                if (inputStream == null) {
                    throw new IOException("Configuration file not found in file system or classpath: " + configPath);
                }
                @SuppressWarnings("unchecked")
                Map<String, Object> config = mapper.readValue(inputStream, Map.class);
                parseConfiguration(config);
            }
        }
        
        logger.info("Configuration loaded successfully");
    }

    @SuppressWarnings("unchecked")
    private void parseConfiguration(Map<String, Object> config) {
        logger.info("Parsing configuration with keys: {}", config.keySet());
        
        // Parse source database configuration
        if (config.containsKey("sourceDatabase")) {
            Map<String, Object> dbConfig = (Map<String, Object>) config.get("sourceDatabase");
            sourceDatabaseConfig = parseDatabaseConfig(dbConfig);
        }

        // Parse target database configuration
        if (config.containsKey("targetDatabase")) {
            Map<String, Object> dbConfig = (Map<String, Object>) config.get("targetDatabase");
            targetDatabaseConfig = parseDatabaseConfig(dbConfig);
        }

        // Parse additional databases
        if (config.containsKey("additionalDatabases")) {
            List<Map<String, Object>> additionalDbs = (List<Map<String, Object>>) config.get("additionalDatabases");
            additionalDatabaseConfigs = new ArrayList<>();
            for (Map<String, Object> dbConfig : additionalDbs) {
                additionalDatabaseConfigs.add(parseDatabaseConfig(dbConfig));
            }
        }

        // Parse report configuration
        if (config.containsKey("report")) {
            Map<String, Object> repConfig = (Map<String, Object>) config.get("report");
            reportConfig = new ReportConfig();
            reportConfig.setOutputDir((String) repConfig.get("outputDir"));
            reportConfig.setTemplateDir((String) repConfig.get("templateDir"));
            reportConfig.setLogLevel((String) repConfig.get("logLevel"));
        }

        // Parse logging configuration
        if (config.containsKey("logging")) {
            Map<String, Object> logConfig = (Map<String, Object>) config.get("logging");
            loggingConfig = new LoggingConfig();
            loggingConfig.setLevel((String) logConfig.get("level"));
            loggingConfig.setPattern((String) logConfig.get("pattern"));
            loggingConfig.setFile((String) logConfig.get("file"));
        }

        // Parse source DynamoDB configuration
        if (config.containsKey("sourceDynamoDB")) {
            Map<String, Object> dbConfig = (Map<String, Object>) config.get("sourceDynamoDB");
            sourceDynamoDBConfig = parseDynamoDBConfig(dbConfig);
        }

        // Parse target DynamoDB configuration
        if (config.containsKey("targetDynamoDB")) {
            Map<String, Object> dbConfig = (Map<String, Object>) config.get("targetDynamoDB");
            targetDynamoDBConfig = parseDynamoDBConfig(dbConfig);
        }

        // Parse S3 configuration
        if (config.containsKey("s3")) {
            Map<String, Object> s3ConfigMap = (Map<String, Object>) config.get("s3");
            s3Config = parseS3Config(s3ConfigMap);
        }

        // Parse S3 source configuration
        if (config.containsKey("s3Source")) {
            Map<String, Object> s3SourceConfigMap = (Map<String, Object>) config.get("s3Source");
            s3SourceConfig = parseS3Config(s3SourceConfigMap);
        }
    }

    private DatabaseConfig parseDatabaseConfig(Map<String, Object> dbConfig) {
        DatabaseConfig config = new DatabaseConfig();
        config.setHost((String) dbConfig.get("host"));
        config.setPort((Integer) dbConfig.get("port"));
        config.setDatabase((String) dbConfig.get("database"));
        config.setUsername((String) dbConfig.get("username"));
        config.setPassword((String) dbConfig.get("password"));
        config.setDriver((String) dbConfig.get("driver"));
        config.setConnectionTimeout((Integer) dbConfig.get("connectionTimeout"));
        config.setMaxPoolSize((Integer) dbConfig.get("maxPoolSize"));
        return config;
    }

    private DynamoDBConfig parseDynamoDBConfig(Map<String, Object> dbConfig) {
        DynamoDBConfig config = new DynamoDBConfig();
        config.setEndpointUrl((String) dbConfig.get("endpointUrl"));
        config.setRegion((String) dbConfig.get("region"));
        config.setAccessKey((String) dbConfig.get("accessKey"));
        config.setSecretKey((String) dbConfig.get("secretKey"));
        String tablePrefix = (String) dbConfig.get("tablePrefix");
        String tableName = (String) dbConfig.get("tableName");
        config.setTablePrefix(tablePrefix != null ? tablePrefix : tableName);
        return config;
    }

    private S3Config parseS3Config(Map<String, Object> s3ConfigMap) {
        S3Config config = new S3Config();
        config.setBucket((String) s3ConfigMap.get("bucket"));
        config.setFolderPrefix((String) s3ConfigMap.get("folderPrefix"));
        config.setRegion((String) s3ConfigMap.get("region"));
        config.setAccessKey((String) s3ConfigMap.get("accessKey"));
        config.setSecretKey((String) s3ConfigMap.get("secretKey"));
        return config;
    }

    // Getters
    public DatabaseConfig getSourceDatabaseConfig() {
        return sourceDatabaseConfig;
    }

    public DatabaseConfig getTargetDatabaseConfig() {
        return targetDatabaseConfig;
    }

    public List<DatabaseConfig> getAdditionalDatabaseConfigs() {
        return additionalDatabaseConfigs != null ? additionalDatabaseConfigs : new ArrayList<>();
    }

    public ReportConfig getReportConfig() {
        return reportConfig;
    }

    public LoggingConfig getLoggingConfig() {
        return loggingConfig;
    }

    public DynamoDBConfig getSourceDynamoDBConfig() {
        return sourceDynamoDBConfig;
    }

    public DynamoDBConfig getTargetDynamoDBConfig() {
        return targetDynamoDBConfig;
    }

    public S3Config getS3Config() {
        return s3Config;
    }

    public S3Config getS3SourceConfig() {
        return s3SourceConfig;
    }

    // Configuration classes
    public static class DatabaseConfig {
        private String host;
        private Integer port;
        private String database;
        private String username;
        private String password;
        private String driver;
        private Integer connectionTimeout;
        private Integer maxPoolSize;

        // Getters and setters
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        
        public Integer getPort() { return port; }
        public void setPort(Integer port) { this.port = port; }
        
        public String getDatabase() { return database; }
        public void setDatabase(String database) { this.database = database; }
        
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getDriver() { return driver; }
        public void setDriver(String driver) { this.driver = driver; }
        
        public Integer getConnectionTimeout() { return connectionTimeout; }
        public void setConnectionTimeout(Integer connectionTimeout) { this.connectionTimeout = connectionTimeout; }
        
        public Integer getMaxPoolSize() { return maxPoolSize; }
        public void setMaxPoolSize(Integer maxPoolSize) { this.maxPoolSize = maxPoolSize; }
    }

    public static class ReportConfig {
        private String outputDir;
        private String templateDir;
        private String logLevel;

        public String getOutputDir() { return outputDir; }
        public void setOutputDir(String outputDir) { this.outputDir = outputDir; }
        
        public String getTemplateDir() { return templateDir; }
        public void setTemplateDir(String templateDir) { this.templateDir = templateDir; }
        
        public String getLogLevel() { return logLevel; }
        public void setLogLevel(String logLevel) { this.logLevel = logLevel; }
    }

    public static class LoggingConfig {
        private String level;
        private String pattern;
        private String file;

        public String getLevel() { return level; }
        public void setLevel(String level) { this.level = level; }
        
        public String getPattern() { return pattern; }
        public void setPattern(String pattern) { this.pattern = pattern; }
        
        public String getFile() { return file; }
        public void setFile(String file) { this.file = file; }
    }

    public static class DynamoDBConfig {
        private String endpointUrl;
        private String region;
        private String accessKey;
        private String secretKey;
        private String tablePrefix;

        public String getEndpointUrl() { return endpointUrl; }
        public void setEndpointUrl(String endpointUrl) { this.endpointUrl = endpointUrl; }
        
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        
        public String getAccessKey() { return accessKey; }
        public void setAccessKey(String accessKey) { this.accessKey = accessKey; }
        
        public String getSecretKey() { return secretKey; }
        public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
        
        public String getTablePrefix() { return tablePrefix; }
        public void setTablePrefix(String tablePrefix) { this.tablePrefix = tablePrefix; }
    }

    public static class S3Config {
        private String bucket;
        private String folderPrefix;
        private String region;
        private String accessKey;
        private String secretKey;

        public String getBucket() { return bucket; }
        public void setBucket(String bucket) { this.bucket = bucket; }
        
        public String getFolderPrefix() { return folderPrefix; }
        public void setFolderPrefix(String folderPrefix) { this.folderPrefix = folderPrefix; }
        
        public String getRegion() { return region; }
        public void setRegion(String region) { this.region = region; }
        
        public String getAccessKey() { return accessKey; }
        public void setAccessKey(String accessKey) { this.accessKey = accessKey; }
        
        public String getSecretKey() { return secretKey; }
        public void setSecretKey(String secretKey) { this.secretKey = secretKey; }
    }
}

