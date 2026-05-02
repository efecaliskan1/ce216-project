@echo off
REM ================================================================
REM  Sport Management System - Windows installer builder
REM  Requires: JDK 17+ with jpackage (bundled in Oracle / Liberica JDK)
REM ================================================================

echo [1/2] Building fat jar with Maven...
call mvn -q clean package
if errorlevel 1 goto :err

echo [2/2] Running jpackage...
jpackage ^
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
