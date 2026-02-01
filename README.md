# Data Migration Automation Framework

A generic, extensible Java framework for data migration validation, schema analysis, and data comparison across multiple data sources. This framework provides a common foundation for validating data migrations between any source and target systems.

## Features

- **Multi-Database Support**: Supports SQL Server, PostgreSQL, and other JDBC-compatible databases
- **DynamoDB Support**: Compare data between DynamoDB tables
- **S3 Support**: Compare objects between S3 buckets
- **Schema Analysis**: Compare and analyze database schemas between source and target
- **Data Comparison**: Validate data migration completeness and accuracy
- **Extensible Architecture**: Plugin-based analyzer system for custom validation logic
- **Comprehensive Reporting**: Generate detailed HTML reports using FreeMarker templates
- **TestNG Integration**: Example tests demonstrating framework usage
- **Configuration-Driven**: YAML-based configuration for easy setup

## Prerequisites

- Java 11 or higher
- Maven 3.6+
- Access to source and target data sources
- Network access to data source servers

## Setup

1. **Clone the repository**:
   ```bash
   git clone <repository-url>
   cd data_migration_automation
   ```

2. **Configure the application**:
   ```bash
   # Copy the example configuration
   cp application-example.yaml application.yaml
   
   # Edit with your actual credentials
   nano application.yaml
   ```

3. **Build the project**:
   ```bash
   mvn clean compile
   ```

## Project Structure

```
src/
├── main/
│   ├── java/com/datamigration/framework/
│   │   ├── analyzer/          # Data analysis components
│   │   │   ├── ComparisonAnalyzer.java
│   │   │   └── DataAnalyzer.java
│   │   ├── config/            # Configuration management
│   │   │   └── ConfigurationManager.java
│   │   ├── connector/         # Data source connectors
│   │   │   ├── DatabaseConnector.java
│   │   │   ├── DynamoDBConnector.java
│   │   │   └── S3Connector.java
│   │   ├── reporting/         # Report generation library
│   │   │   ├── ComparisonReportGenerator.java
│   │   │   ├── ReportGenerator.java
│   │   │   └── SchemaReportGenerator.java
│   │   └── Main.java          # Main entry point
│   └── resources/
│       ├── logback.xml        # Logging configuration
│       └── templates/         # FreeMarker report templates
└── test/
    ├── java/com/datamigration/framework/
    │   └── examples/          # TestNG example tests
    │       ├── TableCountComparisonTest.java
    │       ├── DynamoDBComparisonTest.java
    │       └── S3ObjectComparisonTest.java
    └── resources/
        └── testng.xml         # TestNG suite configuration
```

## Configuration

The application uses YAML configuration files. See `application-example.yaml` for a complete template.

### Database Configuration
```yaml
sourceDatabase:
  host: "source-server"
  port: 1433
  database: "SourceDB"
  username: "source-user"
  password: "source-password"
  driver: "sqlserver"  # or "postgresql"
  connectionTimeout: 30
  maxPoolSize: 10

targetDatabase:
  host: "target-server"
  port: 1433
  database: "TargetDB"
  username: "target-user"
  password: "target-password"
  driver: "sqlserver"
  connectionTimeout: 30
  maxPoolSize: 10
```

### DynamoDB Configuration
```yaml
sourceDynamoDB:
  endpointUrl: "https://dynamodb.region.amazonaws.com"
  region: "us-east-1"
  accessKey: "your-access-key"
  secretKey: "your-secret-key"
  tablePrefix: "your-table-prefix"

targetDynamoDB:
  endpointUrl: "https://dynamodb.region.amazonaws.com"
  region: "us-west-2"
  accessKey: "your-access-key"
  secretKey: "your-secret-key"
  tablePrefix: "your-table-prefix"
```

### S3 Configuration
```yaml
s3Source:
  bucket: "source-bucket-name"
  folderPrefix: "data/"
  region: "us-east-1"
  accessKey: "your-access-key"
  secretKey: "your-secret-key"

s3:
  bucket: "target-bucket-name"
  folderPrefix: "data/"
  region: "us-west-2"
  accessKey: "your-access-key"
  secretKey: "your-secret-key"
```

### Report Configuration
```yaml
report:
  outputDir: "output"
  templateDir: "src/main/resources/templates"
  logLevel: "INFO"
```

## Using the Framework

### Basic Usage

```java
import com.datamigration.framework.config.ConfigurationManager;
import com.datamigration.framework.connector.DatabaseConnector;

// Load configuration
ConfigurationManager config = new ConfigurationManager();
config.loadConfiguration("application.yaml");

// Connect to databases
try (DatabaseConnector source = new DatabaseConnector(config, DatabaseConnector.DatabaseType.SOURCE);
     DatabaseConnector target = new DatabaseConnector(config, DatabaseConnector.DatabaseType.TARGET)) {
    
    // Test connections
    boolean sourceConnected = source.testConnection();
    boolean targetConnected = target.testConnection();
    
    // Perform your analysis...
}
```

### Creating Custom Analyzers

Extend `ComparisonAnalyzer` to create your own comparison logic:

```java
import com.datamigration.framework.analyzer.ComparisonAnalyzer;
import com.datamigration.framework.config.ConfigurationManager;
import com.datamigration.framework.reporting.ComparisonReportGenerator;
import java.util.ArrayList;
import java.util.List;

public class MyCustomAnalyzer extends ComparisonAnalyzer {
    
    public MyCustomAnalyzer(ConfigurationManager config) {
        super(config);
    }
    
    @Override
    public List<ComparisonReportGenerator.ComparisonResult> compare() throws Exception {
        List<ComparisonReportGenerator.ComparisonResult> results = new ArrayList<>();
        
        // Your comparison logic here
        // Access sourceConnector and targetConnector
        // Use reportGenerator to generate reports
        
        return results;
    }
}
```

### Using DynamoDB Connector

```java
import com.datamigration.framework.connector.DynamoDBConnector;

try (DynamoDBConnector source = new DynamoDBConnector(config, DynamoDBConnector.DynamoDBType.SOURCE);
     DynamoDBConnector target = new DynamoDBConnector(config, DynamoDBConnector.DynamoDBType.TARGET)) {
    
    // List tables
    List<String> sourceTables = source.listTables();
    List<String> targetTables = target.listTables();
    
    // Get item counts
    long sourceCount = source.getTableItemCount("my-table");
    long targetCount = target.getTableItemCount("my-table");
}
```

### Using S3 Connector

```java
import com.datamigration.framework.connector.S3Connector;

try (S3Connector source = new S3Connector(config, S3Connector.S3Type.SOURCE);
     S3Connector target = new S3Connector(config, S3Connector.S3Type.TARGET)) {
    
    // List objects
    List<S3Object> sourceObjects = source.listObjects("prefix/");
    List<S3Object> targetObjects = target.listObjects("prefix/");
    
    // Check object existence
    boolean exists = target.objectExists("path/to/object.txt");
}
```

### Generating Reports

```java
import com.datamigration.framework.reporting.ComparisonReportGenerator;

ComparisonReportGenerator generator = new ComparisonReportGenerator("output", "templates");
generator.generateComparisonReport(
    "Source System",
    "Target System",
    comparisonResults,
    "comparison_report.html"
);
```

## Running Tests

The framework includes TestNG example tests demonstrating usage:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=TableCountComparisonTest

# Run using TestNG suite
mvn test -DsuiteXmlFile=src/test/resources/testng.xml

# Run with verbose output
mvn test -Dtest=TableCountComparisonTest -X
```

### Example Tests

- **TableCountComparisonTest**: Compares table row counts between source and target databases
- **DynamoDBComparisonTest**: Compares DynamoDB table item counts
- **S3ObjectComparisonTest**: Compares S3 object counts and existence

These tests serve as both examples and integration tests for the framework.

## Reports

After running analyses or tests, reports are generated in the `output/` directory:

- `table_count_comparison_report.html` - Database table comparison results
- `dynamodb_comparison_report.html` - DynamoDB comparison results
- `s3_comparison_report.html` - S3 object comparison results
- Custom reports based on your analyzers

Reports include:
- Summary statistics (total comparisons, matches, mismatches)
- Detailed comparison results
- Match/mismatch indicators
- Timestamp and system information

## Security

- **Never commit** `application.yaml` or any files containing real credentials
- Use `application-example.yaml` as a template
- The `.gitignore` file excludes sensitive configuration files
- Consider using environment variables for production deployments
- Store credentials in secure vaults or use IAM roles for AWS services

## Extending the Framework

### Adding New Data Sources

1. Create a new connector class implementing `AutoCloseable`
2. Add configuration support in `ConfigurationManager`
3. Create analyzers or tests that use your connector

### Adding New Analyzers

1. Extend `ComparisonAnalyzer` or implement `DataAnalyzer`
2. Implement the `compare()` method with your logic
3. Use the `reportGenerator` to generate reports
4. Add TestNG tests in `src/test/java/` to demonstrate usage

### Custom Report Templates

1. Create FreeMarker templates in `src/main/resources/templates/`
2. Use the `ReportGenerator` classes to generate reports
3. Templates can access data models populated by analyzers

## Troubleshooting

### Common Issues

1. **Database Connection Failed**
   - Verify database credentials in `application.yaml`
   - Check network connectivity to database servers
   - Ensure database is running and accessible
   - Verify firewall rules allow connections

2. **DynamoDB Connection Failed**
   - Verify AWS credentials and region
   - Check IAM permissions for DynamoDB access
   - Ensure endpoint URL is correct

3. **S3 Connection Failed**
   - Verify AWS credentials and region
   - Check IAM permissions for S3 access
   - Ensure bucket names and prefixes are correct

4. **Configuration Not Found**
   - Ensure `application.yaml` exists in project root
   - Check file permissions
   - Verify YAML syntax is correct

5. **Report Generation Failed**
   - Check that output directory is writable
   - Verify template files exist if using custom templates
   - Framework will fall back to simple HTML reports if templates fail

### Debug Mode

Enable debug logging by setting in `application.yaml`:
```yaml
logging:
  level: "DEBUG"
```

## License

This project is provided as-is for data migration validation purposes.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests for new functionality
5. Submit a pull request

## Support

For issues and questions:
- Create an issue in the repository
- Check the generated reports for detailed error information
- Review the example tests in `src/test/java/com/datamigration/framework/examples/`
