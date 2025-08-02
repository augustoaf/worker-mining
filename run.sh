#!/bin/bash

echo "Building and running Equipment Event Publisher..."
echo

# Check if Maven is available
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed or not in PATH"
    echo "Please install Maven and try again"
    exit 1
fi

# Build the project
echo "Building project..."
mvn clean package
if [ $? -ne 0 ]; then
    echo "Error: Build failed"
    exit 1
fi

echo
echo "Build successful! Running application..."
echo "Press Ctrl+C to stop the application"
echo

# Run the application
java -jar target/worker-aggregation-1.0.0.jar 