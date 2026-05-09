@echo off
REM ================================================================
REM  Sport Management System - Windows installer builder
REM  Requires:
REM    - JDK 17+ with jpackage
REM    - WiX Toolset 5+ (with WixToolset.Util.wixext and WixToolset.UI.wixext extensions)
REM  JAVA_HOME must point to your JDK installation folder.
REM ================================================================

REM --- Force English locale to avoid jpackage Turkish-locale bug
set _JAVA_OPTIONS=-Duser.language=en -Duser.country=US

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

echo [1/3] Building application JAR with Maven...
call mvn -q clean package -DskipTests -Djacoco.skip=true
if errorlevel 1 goto :err

echo [2/3] Copying dependencies...
call mvn -q dependency:copy-dependencies -DincludeScope=runtime -DoutputDirectory=target\deps
if errorlevel 1 goto :err

echo [3/3] Running jpackage...
if exist dist rmdir /s /q dist
"%JAVA_HOME%\bin\jpackage" ^
  --type msi ^
  --name "SportManager" ^
  --app-version 3.0.0 ^
  --vendor "Bugyani" ^
  --input target ^
  --main-jar sport-management-3.0.0.jar ^
  --main-class ui.AppLauncher ^
  --module-path "target\deps" ^
  --add-modules javafx.controls,javafx.fxml,javafx.graphics ^
  --dest dist ^
  --win-dir-chooser ^
  --win-menu ^
  --win-shortcut

if errorlevel 1 goto :err

echo.
echo Done. Installer is in the "dist" folder.
goto :eof

:err
echo.
echo Build FAILED. See messages above.
exit /b 1
