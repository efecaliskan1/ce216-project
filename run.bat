@echo off
REM ================================================================
REM  Sport Management System - Launcher
REM  Requirements:
REM    - JDK/JRE 17+ installed (JAVA_HOME or java on PATH)
REM    - Run "mvn clean package" once before using this script
REM ================================================================

REM Copy JavaFX dependencies next to the JAR if not already done
if not exist "target\deps\javafx-controls-21.0.2-win.jar" (
    echo Staging JavaFX libraries...
    call mvn -q dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target\deps
    if errorlevel 1 (
        echo ERROR: Could not stage dependencies. Make sure Maven is on PATH.
        pause
        exit /b 1
    )
)

echo Starting Sport Management System...
java --module-path "target\deps" ^
     --add-modules javafx.controls,javafx.fxml,javafx.graphics ^
     -jar "target\sport-management-3.0.0.jar"

if errorlevel 1 (
    echo.
    echo ERROR: Application failed to start.
    echo Make sure you have run "mvn clean package" first.
    pause
)
