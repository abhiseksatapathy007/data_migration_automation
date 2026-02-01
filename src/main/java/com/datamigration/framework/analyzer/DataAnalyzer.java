package com.datamigration.framework.analyzer;

import com.datamigration.framework.config.ConfigurationManager;

/**
 * Base interface for all data analyzers in the framework.
 * Implementations should analyze data from source and/or target systems.
 */
public interface DataAnalyzer extends AutoCloseable {
    
    /**
     * Performs the analysis.
     * 
     * @return Analysis result object
     * @throws Exception If analysis fails
     */
    AnalysisResult analyze() throws Exception;
    
    /**
     * Gets the configuration manager.
     * 
     * @return ConfigurationManager instance
     */
    ConfigurationManager getConfig();
}

/**
 * Base interface for analysis results.
 */
interface AnalysisResult {
    /**
     * Gets a summary of the analysis.
     * 
     * @return Summary string
     */
    String getSummary();
    
    /**
     * Checks if the analysis indicates success.
     * 
     * @return true if successful, false otherwise
     */
    boolean isSuccessful();
}
