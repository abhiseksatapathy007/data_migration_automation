package com.datamigration.framework.examples;

import com.datamigration.framework.analyzer.ComparisonAnalyzer;
import com.datamigration.framework.config.ConfigurationManager;
import com.datamigration.framework.connector.DatabaseConnector;
import com.datamigration.framework.reporting.ComparisonReportGenerator;
import org.testng.annotations.*;
import org.testng.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * TestNG test for comparing table row counts between source and target databases.
 * This demonstrates how to use the framework to validate data migration.
 */
public class TableCountComparisonTest {
    
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
        tablesToCompare = Arrays.asList("users", "orders", "products");
    }

    @Test(description = "Compare table row counts between source and target databases")
    public void testTableCountComparison() throws Exception {
        // Create analyzer instance
        TableCountComparisonExample analyzer = new TableCountComparisonExample(config, tablesToCompare);
        
        // Run comparison
        List<ComparisonReportGenerator.ComparisonResult> results = analyzer.compare();
        
        // Assertions
        Assert.assertNotNull(results, "Comparison results should not be null");
        Assert.assertFalse(results.isEmpty(), "Comparison results should not be empty");
        
        // Print summary
        long matches = results.stream()
            .filter(ComparisonReportGenerator.ComparisonResult::isMatch)
            .count();
        
        System.out.println("\n=== Table Count Comparison Summary ===");
        System.out.println("Total tables compared: " + results.size());
        System.out.println("Matches: " + matches);
        System.out.println("Mismatches: " + (results.size() - matches));
        
        // Verify all results have valid data
        for (ComparisonReportGenerator.ComparisonResult result : results) {
            Assert.assertNotNull(result.getItemName(), "Item name should not be null");
            Assert.assertNotNull(result.getSourceValue(), "Source value should not be null");
            Assert.assertNotNull(result.getTargetValue(), "Target value should not be null");
        }
        
        // Clean up
        analyzer.close();
    }

    @Test(description = "Test database connections", dependsOnMethods = "testTableCountComparison")
    public void testDatabaseConnections() throws Exception {
        try (DatabaseConnector source = new DatabaseConnector(config, DatabaseConnector.DatabaseType.SOURCE);
             DatabaseConnector target = new DatabaseConnector(config, DatabaseConnector.DatabaseType.TARGET)) {
            
            boolean sourceConnected = source.testConnection();
            boolean targetConnected = target.testConnection();
            
            Assert.assertTrue(sourceConnected, "Source database connection should succeed");
            Assert.assertTrue(targetConnected, "Target database connection should succeed");
        }
    }
}

/**
 * Helper class that extends ComparisonAnalyzer for table count comparison.
 */
class TableCountComparisonExample extends ComparisonAnalyzer {
    
    private final List<String> tablesToCompare;

    public TableCountComparisonExample(ConfigurationManager config, List<String> tablesToCompare) {
        super(config);
        this.tablesToCompare = tablesToCompare;
    }

    @Override
    public List<ComparisonReportGenerator.ComparisonResult> compare() throws Exception {
        List<ComparisonReportGenerator.ComparisonResult> results = new ArrayList<>();
        
        // Compare each table
        for (String tableName : tablesToCompare) {
            long sourceCount = getTableCount(sourceConnector, tableName);
            long targetCount = getTableCount(targetConnector, tableName);
            
            // Create comparison result
            TableCountResult result = new TableCountResult(
                tableName,
                sourceCount,
                targetCount
            );
            
            results.add(result);
            
            System.out.println(String.format("Table: %s | Source: %d | Target: %d | Match: %s",
                tableName, sourceCount, targetCount, result.isMatch() ? "✓" : "✗"));
        }
        
        // Generate report
        reportGenerator.generateComparisonReport(
            sourceConnector.getDatabaseName(),
            targetConnector.getDatabaseName(),
            results,
            "table_count_comparison_report.html"
        );
        
        return results;
    }

    /**
     * Gets the row count for a table.
     */
    private long getTableCount(DatabaseConnector connector, String tableName) {
        try {
            return connector.getTableRowCount(tableName);
        } catch (Exception e) {
            System.err.println("Error getting count for table " + tableName + ": " + e.getMessage());
            return -1;
        }
    }

    /**
     * Implementation of ComparisonResult for table count comparisons.
     */
    private static class TableCountResult implements ComparisonReportGenerator.ComparisonResult {
        private final String tableName;
        private final long sourceCount;
        private final long targetCount;

        public TableCountResult(String tableName, long sourceCount, long targetCount) {
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

