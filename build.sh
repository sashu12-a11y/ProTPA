#!/bin/bash

# ProTPA Build Script
# This script builds the ProTPA plugin using Maven

echo "🚀 Building ProTPA..."

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven is not installed. Please install Maven first."
    exit 1
fi

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "❌ Java is not installed. Please install Java 17+ first."
    exit 1
fi

# Clean and build
echo "📦 Cleaning previous build..."
mvn clean

echo "🔨 Compiling and packaging..."
mvn package

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "✅ Build successful!"
    echo "📁 JAR file created: target/ProTPA.jar"

    # Copy to plugins folder if it exists
    if [ -d "plugins" ]; then
        cp target/ProTPA.jar plugins/
        echo "📋 JAR copied to plugins folder"
    fi
else
    echo "❌ Build failed!"
    exit 1
fi

echo "🎉 Done!"