#!/bin/bash
# Hospital Management System - JavaFX Compilation and Execution Script
# This script compiles and runs the HMS JavaFX application on Linux/Mac

echo "==============================================="
echo "Hospital Management System - JavaFX"
echo "==============================================="
echo ""

# Check if JAVAFX_HOME is set
if [ -z "$JAVAFX_HOME" ]; then
    echo "Error: JAVAFX_HOME environment variable is not set."
    echo "Please set JAVAFX_HOME to your JavaFX SDK directory."
    echo "Example: export JAVAFX_HOME=/path/to/javafx-sdk"
    exit 1
fi

# Check if MYSQL_JAR is set
if [ -z "$MYSQL_JAR" ]; then
    echo "Warning: MYSQL_JAR not set. Looking for mysql-connector in current directory..."
    if ! ls mysql-connector-java-*.jar 1> /dev/null 2>&1; then
        echo "Error: MySQL JDBC driver not found."
        echo "Download from: https://dev.mysql.com/downloads/connector/j/"
        exit 1
    fi
    MYSQL_JAR=$(ls mysql-connector-java-*.jar | head -1)
fi

echo "Compiling HMSBackend.java and HMSApp.java..."
javac --module-path $JAVAFX_HOME/lib --add-modules javafx.controls,javafx.fxml -cp $MYSQL_JAR HMSBackend.java HMSApp.java

if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi

echo ""
echo "Compilation successful!"
echo ""
echo "Running Hospital Management System..."
echo ""

java --module-path $JAVAFX_HOME/lib --add-modules javafx.controls,javafx.fxml -cp .:$MYSQL_JAR HMSApp
