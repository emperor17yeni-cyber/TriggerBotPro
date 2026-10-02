@echo off
setlocal
set GRADLE_VERSION=8.9
set ROOT=%~dp0
set GDIR=%ROOT%.gradle-local
set GHOME=%GDIR%\gradle-%GRADLE_VERSION%
set ZIP=%GDIR%\gradle-%GRADLE_VERSION%-bin.zip

if exist "%GHOME%\bin\gradle.bat" goto build
if not exist "%GDIR%" mkdir "%GDIR%"

echo Gradle %GRADLE_VERSION% indiriliyor...
powershell -NoProfile -ExecutionPolicy Bypass -Command "Invoke-WebRequest -Uri 'https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip' -OutFile '%ZIP%'"
if errorlevel 1 (
    echo Gradle indirilemedi. Internet baglantisini kontrol et veya sistemine Gradle kur.
    exit /b 1
)

powershell -NoProfile -ExecutionPolicy Bypass -Command "Expand-Archive -LiteralPath '%ZIP%' -DestinationPath '%GDIR%' -Force"
if errorlevel 1 (
    echo Gradle arsivi acilamadi.
    exit /b 1
)

del /q "%ZIP%" 2>nul

:build
call "%GHOME%\bin\gradle.bat" build
endlocal
