package com.datamigration.framework.examples;

import com.datamigration.framework.config.ConfigurationManager;
import com.datamigration.framework.connector.S3Connector;
import com.datamigration.framework.reporting.ComparisonReportGenerator;
import org.testng.annotations.*;
import org.testng.Assert;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TestNG test for comparing S3 object counts and existence between source and target buckets.
 * This demonstrates how to use the framework with S3.
 */
public class S3ObjectComparisonTest {
    
    private static ConfigurationManager config;
    private String prefix;

    @BeforeClass
    public static void setUpClass() throws Exception {
        // Load configuration
        config = new ConfigurationManager();
        config.loadConfiguration("application.yaml");
    }

    @BeforeMethod
    public void setUp() {
        // Set prefix for comparison - customize based on your test data
        prefix = null; // Compare all objects, or set to specific prefix like "data/reports/"
    }

    @Test(description = "Compare S3 object counts and existence between source and target buckets")
    public void testS3ObjectComparison() throws Exception {
        S3ObjectComparisonExample example = new S3ObjectComparisonExample(config, prefix);
        List<ComparisonReportGenerator.ComparisonResult> results = example.compare();
        
        // Assertions
        Assert.assertNotNull(results, "Comparison results should not be null");
        Assert.assertFalse(results.isEmpty(), "Comparison results should not be empty");
        
        // Print summary
        long matches = results.stream()
            .filter(ComparisonReportGenerator.ComparisonResult::isMatch)
            .count();
        
        System.out.println("\n=== S3 Comparison Summary ===");
        System.out.println("Total items compared: " + results.size());
        System.out.println("Matches: " + matches);
        System.out.println("Mismatches: " + (results.size() - matches));
        
        // Verify all results have valid data
        for (ComparisonReportGenerator.ComparisonResult result : results) {
            Assert.assertNotNull(result.getItemName(), "Item name should not be null");
            Assert.assertNotNull(result.getSourceValue(), "Source value should not be null");
            Assert.assertNotNull(result.getTargetValue(), "Target value should not be null");
        }
    }

    @Test(description = "Test S3 connections", dependsOnMethods = "testS3ObjectComparison")
    public void testS3Connections() throws Exception {
        try (S3Connector source = new S3Connector(config, S3Connector.S3Type.SOURCE);
             S3Connector target = new S3Connector(config, S3Connector.S3Type.TARGET)) {
            
            boolean sourceConnected = source.testConnection();
            boolean targetConnected = target.testConnection();
            
            Assert.assertTrue(sourceConnected, "Source S3 connection should succeed");
            Assert.assertTrue(targetConnected, "Target S3 connection should succeed");
        }
    }

    /**
     * Helper class for S3 comparison.
     */
    private static class S3ObjectComparisonExample {
        
        private final ConfigurationManager config;
        private final String prefix;

        public S3ObjectComparisonExample(ConfigurationManager config, String prefix) {
            this.config = config;
            this.prefix = prefix;
        }

        /**
         * Compares S3 objects between source and target buckets.
         */
        public List<ComparisonReportGenerator.ComparisonResult> compare() throws Exception {
            List<ComparisonReportGenerator.ComparisonResult> results = new ArrayList<>();
            
            try (S3Connector source = new S3Connector(config, S3Connector.S3Type.SOURCE);
                 S3Connector target = new S3Connector(config, S3Connector.S3Type.TARGET)) {
                
                // Test connections
                System.out.println("Testing S3 connections...");
                boolean sourceConnected = source.testConnection();
                boolean targetConnected = target.testConnection();
                
                if (!sourceConnected || !targetConnected) {
                    throw new Exception("Failed to connect to S3 buckets");
                }
                
                // List objects from both buckets
                System.out.println("Listing objects from source bucket...");
                List<S3Object> sourceObjects = source.listObjects(prefix);
                
                System.out.println("Listing objects from target bucket...");
                List<S3Object> targetObjects = target.listObjects(prefix);
                
                // Create map for quick lookup
                Map<String, S3Object> targetMap = targetObjects.stream()
                    .collect(Collectors.toMap(S3Object::key, obj -> obj));
                
                // Compare object counts
                S3CountResult countResult = new S3CountResult(
                    "Total Objects",
                    sourceObjects.size(),
                    targetObjects.size()
                );
                results.add(countResult);
                
                System.out.println(String.format("Total Objects | Source: %d | Target: %d | Match: %s",
                    sourceObjects.size(), targetObjects.size(), countResult.isMatch() ? "✓" : "✗"));
                
                // Compare individual objects (sample first 10)
                int sampleSize = Math.min(10, Math.max(sourceObjects.size(), targetObjects.size()));
                int compared = 0;
                
                for (S3Object sourceObj : sourceObjects) {
                    if (compared >= sampleSize) break;
                    
                    String key = sourceObj.key();
                    S3Object targetObj = targetMap.get(key);
                    
                    S3ObjectResult objResult = new S3ObjectResult(
                        key,
                        sourceObj.size(),
                        targetObj != null ? targetObj.size() : -1,
                        targetObj != null
                    );
                    
                    results.add(objResult);
                    compared++;
                }
                
                // Generate report
                ComparisonReportGenerator reportGenerator = new ComparisonReportGenerator(
                    config.getReportConfig().getOutputDir(),
                    config.getReportConfig().getTemplateDir()
                );
                
                reportGenerator.generateComparisonReport(
                    "Source S3 (" + source.getBucketName() + ")",
                    "Target S3 (" + target.getBucketName() + ")",
                    results,
                    "s3_comparison_report.html"
                );
            }
            
            return results;
        }

        /**
         * Implementation of ComparisonResult for S3 object count.
         */
        private static class S3CountResult implements ComparisonReportGenerator.ComparisonResult {
            private final String itemName;
            private final long sourceCount;
            private final long targetCount;

            public S3CountResult(String itemName, long sourceCount, long targetCount) {
                this.itemName = itemName;
                this.sourceCount = sourceCount;
                this.targetCount = targetCount;
            }

            @Override
            public String getItemName() {
                return itemName;
            }

            @Override
            public Object getSourceValue() {
                return sourceCount;
            }

            @Override
            public Object getTargetValue() {
                return targetCount;
            }

            @Override
            public boolean isMatch() {
                return sourceCount == targetCount;
            }
        }

        /**
         * Implementation of ComparisonResult for individual S3 objects.
         */
        private static class S3ObjectResult implements ComparisonReportGenerator.ComparisonResult {
            private final String key;
            private final long sourceSize;
            private final long targetSize;
            private final boolean existsInTarget;

            public S3ObjectResult(String key, long sourceSize, long targetSize, boolean existsInTarget) {
                this.key = key;
                this.sourceSize = sourceSize;
                this.targetSize = targetSize;
                this.existsInTarget = existsInTarget;
            }

            @Override
            public String getItemName() {
                return key;
            }

            @Override
            public Object getSourceValue() {
                return sourceSize + " bytes";
            }

            @Override
            public Object getTargetValue() {
                return existsInTarget ? (targetSize + " bytes") : "NOT FOUND";
            }

            @Override
            public boolean isMatch() {
                return existsInTarget && sourceSize == targetSize;
            }
        }
    }
}

