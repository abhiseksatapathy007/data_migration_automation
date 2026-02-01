package com.datamigration.framework.examples;

import com.datamigration.framework.config.ConfigurationManager;
import com.datamigration.framework.connector.DynamoDBConnector;
import com.datamigration.framework.reporting.ComparisonReportGenerator;
import org.testng.annotations.*;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TestNG test for comparing DynamoDB table item counts between source and target.
 * This demonstrates how to use the framework with DynamoDB.
 */
public class DynamoDBComparisonTest {
    
    private static ConfigurationManager config;
    private List<String> tablesToCompare;

    @BeforeClass
    public static void setUpClass() throws Exception {
        // Load configuration
        config = new ConfigurationManager();
        config.loadConfiguration("application.yaml");
    }

    @BeforeMethod
    public void setUp() {
        // Define tables to compare - customize based on your test data
        tablesToCompare = Arrays.asList("users", "sessions", "analytics");
    }

    @Test(description = "Compare DynamoDB table item counts between source and target")
    public void testDynamoDBComparison() throws Exception {
        DynamoDBComparisonExample example = new DynamoDBComparisonExample(config, tablesToCompare);
        List<ComparisonReportGenerator.ComparisonResult> results = example.compare();
        
        // Assertions
        Assert.assertNotNull(results, "Comparison results should not be null");
        Assert.assertFalse(results.isEmpty(), "Comparison results should not be empty");
        
        // Print summary
        long matches = results.stream()
            .filter(ComparisonReportGenerator.ComparisonResult::isMatch)
            .count();
        
        System.out.println("\n=== DynamoDB Comparison Summary ===");
        System.out.println("Total tables compared: " + results.size());
        System.out.println("Matches: " + matches);
        System.out.println("Mismatches: " + (results.size() - matches));
        
        // Verify all results have valid data
        for (ComparisonReportGenerator.ComparisonResult result : results) {
            Assert.assertNotNull(result.getItemName(), "Item name should not be null");
            Assert.assertNotNull(result.getSourceValue(), "Source value should not be null");
            Assert.assertNotNull(result.getTargetValue(), "Target value should not be null");
        }
    }

    @Test(description = "Test DynamoDB connections", dependsOnMethods = "testDynamoDBComparison")
    public void testDynamoDBConnections() throws Exception {
        try (DynamoDBConnector source = new DynamoDBConnector(config, DynamoDBConnector.DynamoDBType.SOURCE);
             DynamoDBConnector target = new DynamoDBConnector(config, DynamoDBConnector.DynamoDBType.TARGET)) {
            
            boolean sourceConnected = source.testConnection();
            boolean targetConnected = target.testConnection();
            
            Assert.assertTrue(sourceConnected, "Source DynamoDB connection should succeed");
            Assert.assertTrue(targetConnected, "Target DynamoDB connection should succeed");
        }
    }

    /**
     * Helper class for DynamoDB comparison.
     */
    private static class DynamoDBComparisonExample {
        
        private final ConfigurationManager config;
        private final List<String> tablesToCompare;

        public DynamoDBComparisonExample(ConfigurationManager config, List<String> tablesToCompare) {
            this.config = config;
            this.tablesToCompare = tablesToCompare;
        }

        /**
         * Compares DynamoDB tables between source and target.
         */
        public List<ComparisonReportGenerator.ComparisonResult> compare() throws Exception {
            List<ComparisonReportGenerator.ComparisonResult> results = new ArrayList<>();
            
            try (DynamoDBConnector source = new DynamoDBConnector(config, DynamoDBConnector.DynamoDBType.SOURCE);
                 DynamoDBConnector target = new DynamoDBConnector(config, DynamoDBConnector.DynamoDBType.TARGET)) {
                
                // Test connections
                System.out.println("Testing DynamoDB connections...");
                boolean sourceConnected = source.testConnection();
                boolean targetConnected = target.testConnection();
                
                if (!sourceConnected || !targetConnected) {
                    throw new Exception("Failed to connect to DynamoDB instances");
                }
                
                // Compare each table
                for (String tableName : tablesToCompare) {
                    long sourceCount = source.getTableItemCount(tableName);
                    long targetCount = target.getTableItemCount(tableName);
                    
                    // Create comparison result
                    DynamoDBCountResult result = new DynamoDBCountResult(
                        tableName,
                        sourceCount,
                        targetCount
                    );
                    
                    results.add(result);
                    
                    System.out.println(String.format("Table: %s | Source: %d | Target: %d | Match: %s",
                        tableName, sourceCount, targetCount, result.isMatch() ? "✓" : "✗"));
                }
                
                // Generate report
                ComparisonReportGenerator reportGenerator = new ComparisonReportGenerator(
                    config.getReportConfig().getOutputDir(),
                    config.getReportConfig().getTemplateDir()
                );
                
                reportGenerator.generateComparisonReport(
                    "Source DynamoDB (" + source.getRegion() + ")",
                    "Target DynamoDB (" + target.getRegion() + ")",
                    results,
                    "dynamodb_comparison_report.html"
                );
            }
            
            return results;
        }

        /**
         * Implementation of ComparisonResult for DynamoDB count comparisons.
         */
        private static class DynamoDBCountResult implements ComparisonReportGenerator.ComparisonResult {
            private final String tableName;
            private final long sourceCount;
            private final long targetCount;

            public DynamoDBCountResult(String tableName, long sourceCount, long targetCount) {
                this.tableName = tableName;
                this.sourceCount = sourceCount;
                this.targetCount = targetCount;
            }

            @Override
            public String getItemName() {
                return tableName;
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
                return sourceCount == targetCount && sourceCount >= 0;
            }
        }
    }
}

