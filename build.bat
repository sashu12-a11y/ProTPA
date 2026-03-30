@echo off
REM ProTPA Build Script for Windows
REM This script builds the ProTPA plugin using Maven

echo 🚀 Building ProTPA...

REM Check if Maven is installed
mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Maven is not installed. Please install Maven first.
    pause
    exit /b 1
)

REM Check if Java is installed
java --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Java is not installed. Please install Java 17+ first.
    pause
    exit /b 1
)

REM Clean and build
echo 📦 Cleaning previous build...
call mvn clean

echo 🔨 Compiling and packaging...
call mvn package

REM Check if build was successful
if %errorlevel% equ 0 (
    echo ✅ Build successful!
    echo 📁 JAR file created: target\ProTPA.jar

    REM Copy to plugins folder if it exists
    if exist "plugins\" (
        copy target\ProTPA.jar plugins\
        echo 📋 JAR copied to plugins folder
    )
) else (
    echo ❌ Build failed!
    pause
    exit /b 1
)

echo 🎉 Done!
pause