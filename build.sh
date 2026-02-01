#!/bin/bash

# EU Data Migration Automation - Build Script
# This script compiles, tests, and packages the Java application

set -e  # Exit on any error

echo "🔨 Building EU Data Migration Automation Framework..."

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven 3.6+ first."
    exit 1
fi

# Check if Java 11+ is available
JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo "❌ Java 11 or higher is required. Current version: $JAVA_VERSION"
    exit 1
fi

echo "✅ Java version: $(java -version 2>&1 | head -n 1)"
echo "✅ Maven version: $(mvn -version | head -n 1)"

# Clean previous builds
echo "🧹 Cleaning previous builds..."
mvn clean

# Compile the project
echo "🔧 Compiling project..."
mvn compile

# Run tests
echo "🧪 Running tests..."
mvn test

# Package the application
echo "📦 Packaging application..."
mvn package

# Create output directory
echo "📁 Creating output directory..."
mkdir -p output
mkdir -p logs

echo "✅ Build completed successfully!"
echo ""
echo "📋 Available commands:"
echo "  Test connection:"
echo "    java -jar target/eu-data-migration-automation-1.0.0-jar-with-dependencies.jar --test-connection"
echo ""
echo "  Generate reports:"
echo "    java -jar target/eu-data-migration-automation-1.0.0-jar-with-dependencies.jar --complete-report"
echo "    java -jar target/eu-data-migration-automation-1.0.0-jar-with-dependencies.jar --schema-only"
echo "    java -jar target/eu-data-migration-automation-1.0.0-jar-with-dependencies.jar --customer-only"
echo ""
echo "  Show help:"
echo "    java -jar target/eu-data-migration-automation-1.0.0-jar-with-dependencies.jar --help"
echo ""
echo "📁 Generated files:"
echo "  - JAR file: target/eu-data-migration-automation-1.0.0-jar-with-dependencies.jar"
echo "  - Reports: output/"
echo "  - Logs: logs/" 