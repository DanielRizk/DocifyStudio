@echo off
setlocal enabledelayedexpansion

set "javaMinVersion=17"
set "gradleDir=C:\Gradle"
set "gradleVersion=gradle-8.6"


:: Use the correct delimiters and handle the output of 'java -version' to capture the major version directly
for /f tokens^=2-5^ delims^=.-_^" %%a in ('java -version 2^>^&1') do (
    set "full_version=%%a"
    goto versionFound
)

:versionFound
::echo Detected Java version: !full_version!

:: Extract the major version number from the full version string
for /f "tokens=1 delims=." %%i in ("!full_version!") do set /a "major_version=%%i"

:: Check if Java version is 17 or higher
if !major_version! LSS %javaMinVersion% (
    echo Java %javaMinVersion% or higher is not installed.
    exit /b 1
) else (
    echo Java version !major_version! found!.
	goto checkGradle
)

:checkGradle
:: Check if Gradle is installed by attempting to get its version
cmd /c gradle -v >nul 2>&1
set gradleCheckErrorLevel=%errorlevel%

if %gradleCheckErrorLevel% neq 0 (
    echo Gradle is not installed.
    echo Do you want to install Gradle and proceed with the build [y/n]?
    set /p UserInput=
    if /I "!UserInput!"=="y" goto installGradle
    echo Installation cancelled. Exiting script.
    exit /b 1
)

gradle clean build
exit /b 0

:installGradle
echo Installing Gradle...
:: Ensure the directory exists for Gradle installation
if not exist %gradleDir% mkdir %gradleDir%

:: Download Gradle.
curl -L --connect-timeout 60 --max-time 3600 -o "%gradleDir%\gradle.zip" https://github.com/gradle/gradle-distributions/releases/download/v8.6.0/%gradleVersion%-bin.zip
if not %errorlevel% == 0 (
    echo Failed to download Gradle. Check your internet connection or URL and try again.
    exit /b 1
)

:: Extract the ZIP file.
echo Extracting Gradle...
tar -xf "%gradleDir%\gradle.zip" -C "%gradleDir%"
if not %errorlevel% == 0 (
    echo Failed to extract Gradle. Check the ZIP file and try again.
    exit /b 1
)

del "%gradleDir%\gradle.zip"

:: Add Gradle to your PATH environment variable manually or using a command like below.
:: This updates the system PATH for future sessions.
echo Setting path to user env vars "%gradleDir%\%gradleVersion%\bin"
powershell -Command "[System.Environment]::SetEnvironmentVariable('Path', [System.Environment]::GetEnvironmentVariable('Path', [System.EnvironmentVariableTarget]::User) + ';%gradleDir%\%gradleVersion%\bin', [System.EnvironmentVariableTarget]::User)"


echo Gradle has been installed. 
echo Please close this CMD window and start a new one and run "build.bat" again.

exit /b 0
