@echo off
REM ================================================================
REM  Sport Management System - Windows installer builder
REM  Requires: JDK 17+ with jpackage (bundled in Oracle / Liberica JDK)
REM  JAVA_HOME must point to your JDK installation folder.
REM ================================================================

if "%JAVA_HOME%"=="" (
  echo ERROR: JAVA_HOME is not set. Please set JAVA_HOME to your JDK install folder.
  echo Example: setx JAVA_HOME "C:\Program Files\Java\jdk-25"
  exit /b 1
)

if not exist "%JAVA_HOME%\bin\jpackage.exe" (
  echo ERROR: jpackage.exe not found at "%JAVA_HOME%\bin\jpackage.exe"
  echo Make sure JAVA_HOME points to a JDK 17+ that includes jpackage.
  exit /b 1
)

echo [1/2] Building fat jar with Maven...
call mvn -q clean package -DskipTests -Djacoco.skip=true
if errorlevel 1 goto :err

echo [2/2] Running jpackage...
"%JAVA_HOME%\bin\jpackage" ^
  --type msi ^
  --name "SportManager" ^
  --app-version 3.0.0 ^
  --vendor "Bugyani Team" ^
  --input target ^
  --main-jar sport-management-3.0.0.jar ^
  --main-class ui.AppLauncher ^
  --dest dist ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut ^
  --icon src\main\resources\logos\app.ico

if errorlevel 1 goto :err

echo.
echo Done. Installer is in the "dist" folder.
goto :eof

:err
echo.
echo Build FAILED. See messages above.
exit /b 1
