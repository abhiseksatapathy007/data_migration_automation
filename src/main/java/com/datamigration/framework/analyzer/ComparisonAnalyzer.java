package com.datamigration.framework.analyzer;

import com.datamigration.framework.config.ConfigurationManager;
import com.datamigration.framework.connector.DatabaseConnector;
import com.datamigration.framework.reporting.ComparisonReportGenerator;

import java.util.List;

/**
 * Base class for comparison analyzers that compare data between source and target systems.
 */
public abstract class ComparisonAnalyzer implements DataAnalyzer {
    protected final ConfigurationManager config;
    protected final DatabaseConnector sourceConnector;
    protected final DatabaseConnector targetConnector;
    protected final ComparisonReportGenerator reportGenerator;

    public ComparisonAnalyzer(ConfigurationManager config) {
        this.config = config;
        this.sourceConnector = new DatabaseConnector(config, DatabaseConnector.DatabaseType.SOURCE);
        this.targetConnector = new DatabaseConnector(config, DatabaseConnector.DatabaseType.TARGET);
        
        String outputDir = config.getReportConfig() != null ? 
            config.getReportConfig().getOutputDir() : "output";
        String templateDir = config.getReportConfig() != null ? 
            config.getReportConfig().getTemplateDir() : null;
        this.reportGenerator = new ComparisonReportGenerator(outputDir, templateDir);
    }

    /**
     * Compares data between source and target systems.
     * 
     * @return List of comparison results
     * @throws Exception If comparison fails
     */
    public abstract List<ComparisonReportGenerator.ComparisonResult> compare() throws Exception;

    @Override
    public AnalysisResult analyze() throws Exception {
        List<ComparisonReportGenerator.ComparisonResult> results = compare();
        return new ComparisonAnalysisResult(results);
    }

    @Override
    public ConfigurationManager getConfig() {
        return config;
    }

    @Override
    public void close() throws Exception {
        if (sourceConnector != null) {
            sourceConnector.close();
        }
        if (targetConnector != null) {
            targetConnector.close();
        }
    }

    /**
     * Implementation of AnalysisResult for comparison analyses.
     */
    private static class ComparisonAnalysisResult implements com.datamigration.framework.analyzer.AnalysisResult {
        private final List<ComparisonReportGenerator.ComparisonResult> results;

        public ComparisonAnalysisResult(List<ComparisonReportGenerator.ComparisonResult> results) {
            this.results = results;
        }

        @Override
        public String getSummary() {
            if (results == null || results.isEmpty()) {
                return "No comparison results";
            }
            long matches = results.stream()
                .filter(ComparisonReportGenerator.ComparisonResult::isMatch)
                .count();
            return String.format("Total: %d, Matches: %d, Mismatches: %d", 
                results.size(), matches, results.size() - matches);
        }

        @Override
        public boolean isSuccessful() {
            if (results == null || results.isEmpty()) {
                return false;
            }
            return results.stream().allMatch(ComparisonReportGenerator.ComparisonResult::isMatch);
        }
    }
}

