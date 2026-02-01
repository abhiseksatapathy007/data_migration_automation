package com.datamigration.framework.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Generates comparison reports for data migration validation.
 * Compares data between source and target systems.
 */
public class ComparisonReportGenerator extends ReportGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ComparisonReportGenerator.class);

    public ComparisonReportGenerator(String outputDirectory, String templateDirectory) {
        super(outputDirectory, templateDirectory);
    }

    /**
     * Generates a database comparison report.
     * 
     * @param sourceName Name of the source system
     * @param targetName Name of the target system
     * @param comparisonResults List of comparison results
     * @param outputFileName Output file name
     * @throws IOException If report generation fails
     */
    public void generateComparisonReport(String sourceName, String targetName, 
                                         List<? extends ComparisonResult> comparisonResults,
                                         String outputFileName) throws IOException {
        logger.info("Generating comparison report: {} vs {}", sourceName, targetName);
        
        Map<String, Object> dataModel = createBaseDataModel();
        dataModel.put("reportType", "Data Comparison Report");
        dataModel.put("sourceName", sourceName);
        dataModel.put("targetName", targetName);
        dataModel.put("comparisonResults", comparisonResults);
        
        // Calculate summary statistics
        long totalComparisons = comparisonResults.size();
        long matches = comparisonResults.stream().filter(ComparisonResult::isMatch).count();
        long mismatches = totalComparisons - matches;
        
        dataModel.put("totalComparisons", totalComparisons);
        dataModel.put("matches", matches);
        dataModel.put("mismatches", mismatches);
        dataModel.put("matchPercentage", totalComparisons > 0 ? (matches * 100.0 / totalComparisons) : 0.0);
        
        try {
            generateReport("comparison_report.ftl", outputFileName, dataModel);
        } catch (Exception e) {
            logger.warn("Template-based report generation failed, generating simple HTML report", e);
            generateSimpleComparisonReport(sourceName, targetName, comparisonResults, outputFileName);
        }
    }

    /**
     * Generates a simple HTML comparison report.
     */
    private void generateSimpleComparisonReport(String sourceName, String targetName,
                                               List<? extends ComparisonResult> comparisonResults,
                                               String outputFileName) throws IOException {
        StringBuilder content = new StringBuilder();
        
        content.append("<div class='summary'>");
        content.append("<h2>Comparison Summary</h2>");
        content.append("<p><strong>Source:</strong> ").append(escapeHtml(sourceName)).append("</p>");
        content.append("<p><strong>Target:</strong> ").append(escapeHtml(targetName)).append("</p>");
        
        long totalComparisons = comparisonResults.size();
        long matches = comparisonResults.stream().filter(ComparisonResult::isMatch).count();
        long mismatches = totalComparisons - matches;
        
        content.append("<p><strong>Total Comparisons:</strong> ").append(totalComparisons).append("</p>");
        content.append("<p><strong>Matches:</strong> ").append(matches).append("</p>");
        content.append("<p><strong>Mismatches:</strong> ").append(mismatches).append("</p>");
        if (totalComparisons > 0) {
            content.append("<p><strong>Match Rate:</strong> ")
                   .append(String.format("%.2f%%", matches * 100.0 / totalComparisons))
                   .append("</p>");
        }
        content.append("</div>");
        
        content.append("<h2>Comparison Details</h2>");
        content.append("<table>");
        content.append("<tr><th>Item</th><th>Source Value</th><th>Target Value</th><th>Status</th></tr>");
        
        for (ComparisonResult result : comparisonResults) {
            content.append("<tr>");
            content.append("<td>").append(escapeHtml(result.getItemName())).append("</td>");
            content.append("<td>").append(escapeHtml(String.valueOf(result.getSourceValue()))).append("</td>");
            content.append("<td>").append(escapeHtml(String.valueOf(result.getTargetValue()))).append("</td>");
            String status = result.isMatch() ? 
                "<span style='color: green;'>✓ Match</span>" : 
                "<span style='color: red;'>✗ Mismatch</span>";
            content.append("<td>").append(status).append("</td>");
            content.append("</tr>");
        }
        
        content.append("</table>");
        
        generateSimpleHtmlReport("Data Comparison Report", content.toString(), outputFileName);
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    /**
     * Interface for comparison results.
     */
    public interface ComparisonResult {
        String getItemName();
        Object getSourceValue();
        Object getTargetValue();
        boolean isMatch();
    }
}

