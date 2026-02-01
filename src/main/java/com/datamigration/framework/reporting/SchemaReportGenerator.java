package com.datamigration.framework.reporting;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Generates schema analysis reports for database structures.
 */
public class SchemaReportGenerator extends ReportGenerator {
    private static final Logger logger = LoggerFactory.getLogger(SchemaReportGenerator.class);

    public SchemaReportGenerator(String outputDirectory, String templateDirectory) {
        super(outputDirectory, templateDirectory);
    }

    /**
     * Generates a schema analysis report.
     * 
     * @param schemaName Name of the schema/database
     * @param tables List of table information
     * @param summary Summary statistics
     * @param outputFileName Output file name
     * @throws IOException If report generation fails
     */
    public void generateSchemaReport(String schemaName, List<? extends TableInfo> tables,
                                     Map<String, Object> summary, String outputFileName) throws IOException {
        logger.info("Generating schema report for: {}", schemaName);
        
        Map<String, Object> dataModel = createBaseDataModel();
        dataModel.put("reportType", "Schema Analysis Report");
        dataModel.put("schemaName", schemaName);
        dataModel.put("tables", tables);
        dataModel.put("summary", summary);
        
        // Calculate additional statistics
        if (tables != null) {
            dataModel.put("totalTables", tables.size());
            long totalRows = tables.stream().mapToLong(TableInfo::getRowCount).sum();
            long totalSize = tables.stream().mapToLong(TableInfo::getSizeBytes).sum();
            dataModel.put("totalRows", totalRows);
            dataModel.put("totalSize", totalSize);
            dataModel.put("totalSizeFormatted", formatBytes(totalSize));
        }
        
        try {
            generateReport("schema_report.ftl", outputFileName, dataModel);
        } catch (Exception e) {
            logger.warn("Template-based report generation failed, generating simple HTML report", e);
            generateSimpleSchemaReport(schemaName, tables, summary, outputFileName);
        }
    }

    /**
     * Generates a simple HTML schema report.
     */
    private void generateSimpleSchemaReport(String schemaName, List<? extends TableInfo> tables,
                                           Map<String, Object> summary, String outputFileName) throws IOException {
        StringBuilder content = new StringBuilder();
        
        content.append("<div class='summary'>");
        content.append("<h2>Schema Summary</h2>");
        content.append("<p><strong>Schema:</strong> ").append(escapeHtml(schemaName)).append("</p>");
        if (tables != null) {
            content.append("<p><strong>Total Tables:</strong> ").append(tables.size()).append("</p>");
            long totalRows = tables.stream().mapToLong(TableInfo::getRowCount).sum();
            long totalSize = tables.stream().mapToLong(TableInfo::getSizeBytes).sum();
            content.append("<p><strong>Total Rows:</strong> ").append(totalRows).append("</p>");
            content.append("<p><strong>Total Size:</strong> ").append(formatBytes(totalSize)).append("</p>");
        }
        if (summary != null) {
            summary.forEach((key, value) -> 
                content.append("<p><strong>").append(key).append(":</strong> ").append(value).append("</p>"));
        }
        content.append("</div>");
        
        if (tables != null && !tables.isEmpty()) {
            content.append("<h2>Tables</h2>");
            content.append("<table>");
            content.append("<tr><th>Table Name</th><th>Schema</th><th>Row Count</th><th>Size</th></tr>");
            
            for (TableInfo table : tables) {
                content.append("<tr>");
                content.append("<td>").append(escapeHtml(table.getName())).append("</td>");
                content.append("<td>").append(escapeHtml(table.getSchema())).append("</td>");
                content.append("<td>").append(table.getRowCount()).append("</td>");
                content.append("<td>").append(formatBytes(table.getSizeBytes())).append("</td>");
                content.append("</tr>");
            }
            
            content.append("</table>");
        }
        
        generateSimpleHtmlReport("Schema Analysis Report", content.toString(), outputFileName);
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
     * Interface for table information.
     */
    public interface TableInfo {
        String getName();
        String getSchema();
        long getRowCount();
        long getSizeBytes();
    }
}

