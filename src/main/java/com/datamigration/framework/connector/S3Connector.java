package com.datamigration.framework.connector;

import com.datamigration.framework.config.ConfigurationManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Generic S3 connector for data migration framework.
 * Supports connecting to source and target S3 buckets.
 */
public class S3Connector implements AutoCloseable {
    private static final Logger logger = LoggerFactory.getLogger(S3Connector.class);
    
    public enum S3Type {
        SOURCE, TARGET
    }
    
    private final ConfigurationManager config;
    private final S3Type s3Type;
    private S3Client client;

    public S3Connector(ConfigurationManager config) {
        this(config, S3Type.SOURCE);
    }

    public S3Connector(ConfigurationManager config, S3Type s3Type) {
        this.config = config;
        this.s3Type = s3Type;
    }

    /**
     * Gets or creates the S3 client.
     */
    public S3Client getClient() {
        if (client == null) {
            ConfigurationManager.S3Config s3Config = getS3Config();
            
            if (s3Config == null) {
                throw new IllegalStateException("S3 configuration not found for " + s3Type);
            }
            
            AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
                s3Config.getAccessKey(),
                s3Config.getSecretKey()
            );
            
            var builder = S3Client.builder()
                .region(Region.of(s3Config.getRegion()))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds));
            
            client = builder.build();
            logger.info("S3 client created for {} (region: {}, bucket: {})", 
                s3Type, s3Config.getRegion(), s3Config.getBucket());
        }
        return client;
    }

    private ConfigurationManager.S3Config getS3Config() {
        switch (s3Type) {
            case SOURCE:
                return config.getS3SourceConfig();
            case TARGET:
                return config.getS3Config();
            default:
                return config.getS3SourceConfig();
        }
    }

    /**
     * Tests the S3 connection by listing buckets.
     */
    public boolean testConnection() {
        try {
            ListBucketsResponse response = getClient().listBuckets();
            logger.info("S3 connection test successful. Found {} buckets", response.buckets().size());
            return true;
        } catch (Exception e) {
            logger.error("S3 connection test failed: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Lists all objects in the configured bucket with the specified prefix.
     */
    public List<S3Object> listObjects(String prefix) {
        List<S3Object> objects = new ArrayList<>();
        try {
            ConfigurationManager.S3Config s3Config = getS3Config();
            String fullPrefix = (s3Config.getFolderPrefix() != null ? s3Config.getFolderPrefix() : "") + 
                               (prefix != null ? prefix : "");
            
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                .bucket(s3Config.getBucket())
                .prefix(fullPrefix)
                .build();
            
            ListObjectsV2Response response = getClient().listObjectsV2(request);
            objects.addAll(response.contents());
            
            // Handle pagination
            while (response.isTruncated()) {
                request = ListObjectsV2Request.builder()
                    .bucket(s3Config.getBucket())
                    .prefix(fullPrefix)
                    .continuationToken(response.nextContinuationToken())
                    .build();
                response = getClient().listObjectsV2(request);
                objects.addAll(response.contents());
            }
        } catch (Exception e) {
            logger.error("Failed to list S3 objects with prefix {}: {}", prefix, e.getMessage());
        }
        return objects;
    }

    /**
     * Lists all objects in the configured bucket.
     */
    public List<S3Object> listAllObjects() {
        return listObjects(null);
    }

    /**
     * Gets the count of objects in the bucket with the specified prefix.
     */
    public long getObjectCount(String prefix) {
        return listObjects(prefix).size();
    }

    /**
     * Checks if an object exists in the bucket.
     */
    public boolean objectExists(String key) {
        try {
            ConfigurationManager.S3Config s3Config = getS3Config();
            HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(s3Config.getBucket())
                .key(key)
                .build();
            
            getClient().headObject(request);
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            logger.error("Failed to check if object exists {}: {}", key, e.getMessage());
            return false;
        }
    }

    /**
     * Gets object metadata.
     */
    public HeadObjectResponse getObjectMetadata(String key) {
        try {
            ConfigurationManager.S3Config s3Config = getS3Config();
            HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(s3Config.getBucket())
                .key(key)
                .build();
            
            return getClient().headObject(request);
        } catch (Exception e) {
            logger.error("Failed to get object metadata for {}: {}", key, e.getMessage());
            return null;
        }
    }

    /**
     * Gets the bucket name from configuration.
     */
    public String getBucketName() {
        ConfigurationManager.S3Config s3Config = getS3Config();
        return s3Config != null ? s3Config.getBucket() : null;
    }

    /**
     * Gets the folder prefix from configuration.
     */
    public String getFolderPrefix() {
        ConfigurationManager.S3Config s3Config = getS3Config();
        return s3Config != null ? s3Config.getFolderPrefix() : null;
    }

    /**
     * Gets the region from configuration.
     */
    public String getRegion() {
        ConfigurationManager.S3Config s3Config = getS3Config();
        return s3Config != null ? s3Config.getRegion() : null;
    }

    @Override
    public void close() {
        if (client != null) {
            client.close();
            logger.info("S3 client closed");
        }
    }
}

