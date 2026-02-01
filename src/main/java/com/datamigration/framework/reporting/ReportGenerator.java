package com.datamigration.framework.reporting;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic report generator using FreeMarker templates.
 * This is the base class for all report generation in the framework.
 */
public class ReportGenerator {
    private static final Logger logger = LoggerFactory.getLogger(ReportGenerator.class);
    
    private final String outputDirectory;
    private final String templateDirectory;
    private final freemarker.template.Configuration freemarkerConfig;

    public ReportGenerator(String outputDirectory, String templateDirectory) {
        this.outputDirectory = outputDirectory;
        this.templateDirectory = templateDirectory;
        this.freemarkerConfig = initializeFreeMarker();
    }

    /**
     * Initializes FreeMarker configuration.
     */
    private freemarker.template.Configuration initializeFreeMarker() {
        freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_32);
        
        // Set template directory
        if (templateDirectory != null) {
            try {
                Path templatePath = Paths.get(templateDirectory);
                if (Files.exists(templatePath)) {
                    cfg.setDirectoryForTemplateLoading(templatePath.toFile());
                } else {
                    logger.warn("Template directory not found: {}. Using classpath.", templateDirectory);
                    cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates");
                }
            } catch (IOException e) {
                logger.warn("Could not set template directory: {}. Using classpath.", templateDirectory, e);
                cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates");
            }
        } else {
            cfg.setClassLoaderForTemplateLoading(getClass().getClassLoader(), "templates");
        }
        
        cfg.setDefaultEncoding("UTF-8");
        return cfg;
    }

    /**
     * Generates a report using the specified template.
     * 
     * @param templateName Name of the FreeMarker template file
     * @param outputFileName Name of the output file
     * @param dataModel Data model to populate the template
     * @throws IOException If file operations fail
     * @throws TemplateException If template processing fails
     */
    public void generateReport(String templateName, String outputFileName, Map<String, Object> dataModel) 
            throws IOException, TemplateException {
        
        // Ensure output directory exists
        Path outputPath = Paths.get(outputDirectory);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        
        // Add common metadata to data model
        if (!dataModel.containsKey("generatedAt")) {
            dataModel.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        }
        
        // Get template
        Template template = freemarkerConfig.getTemplate(templateName);
        
        // Generate report
        Path outputFile = outputPath.resolve(outputFileName);
        try (Writer writer = new FileWriter(outputFile.toFile())) {
            template.process(dataModel, writer);
        }
        
        logger.info("Report generated: {}", outputFile.toAbsolutePath());
    }

    /**
     * Generates a simple HTML report when templates are not available.
     * 
     * @param title Report title
     * @param content HTML content
     * @param outputFileName Output file name
     * @throws IOException If file operations fail
     */
    public void generateSimpleHtmlReport(String title, String content, String outputFileName) throws IOException {
        logger.info("Generating simple HTML report: {}", title);
        
        Path outputPath = Paths.get(outputDirectory);
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
        }
        
        Path outputFile = outputPath.resolve(outputFileName);
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile.toFile()))) {
            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<head>");
            writer.println("<title>" + escapeHtml(title) + "</title>");
            writer.println("<meta charset=\"UTF-8\">");
            writer.println("<style>");
            writer.println("body { font-family: Arial, sans-serif; margin: 20px; background-color: #f5f5f5; }");
            writer.println(".container { max-width: 1200px; margin: 0 auto; background-color: white; padding: 20px; border-radius: 5px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }");
            writer.println("h1 { color: #333; border-bottom: 2px solid #007bff; padding-bottom: 10px; }");
            writer.println("table { border-collapse: collapse; width: 100%; margin: 20px 0; }");
            writer.println("th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }");
            writer.println("th { background-color: #007bff; color: white; }");
            writer.println("tr:nth-child(even) { background-color: #f9f9f9; }");
            writer.println(".summary { background-color: #e7f3ff; padding: 15px; margin: 10px 0; border-radius: 5px; }");
            writer.println(".info { color: #666; font-size: 0.9em; margin: 10px 0; }");
            writer.println("</style>");
            writer.println("</head>");
            writer.println("<body>");
            writer.println("<div class='container'>");
            writer.println("<h1>" + escapeHtml(title) + "</h1>");
            writer.println("<div class='info'>Generated at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "</div>");
            writer.println(content);
            writer.println("</div>");
            writer.println("</body>");
            writer.println("</html>");
        }
        
        logger.info("Simple HTML report generated: {}", outputFile.toAbsolutePath());
    }

    /**
     * Creates a basic data model with common metadata.
     */
    public Map<String, Object> createBaseDataModel() {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return dataModel;
    }

    /**
     * Escapes HTML special characters.
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }

    /**
     * Formats bytes into human-readable format.
     */
    protected String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp-1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

    public String getOutputDirectory() {
        return outputDirectory;
    }

    public String getTemplateDirectory() {
        return templateDirectory;
    }
}

