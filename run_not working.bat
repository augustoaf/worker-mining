@echo off
echo Building and running Equipment Event Publisher...
echo.

REM Check if Maven is available
mvn -version >nul 2>&1
if errorlevel 1 (
    echo Error: Maven is not installed or not in PATH
    echo Please install Maven and try again
    pause
    exit /b 1
)

REM Build the project
echo Building project...
mvn clean package
if errorlevel 1 (
    echo Error: Build failed
    pause
    exit /b 1
)

echo.
echo Build successful! Running application...
echo Press Ctrl+C to stop the application
echo.

REM Run the application
java -jar target/worker-mining-1.0.0.jar

pause 