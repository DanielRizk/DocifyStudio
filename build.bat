@echo off
gradle clean build

set "OutDir=build\libs"

:: Navigate to the batch file's directory
cd /d "%OutDir%"

:: Find and delete files ending with -all.jar
for %%f in (*-all.jar) do (
    echo Found: %%f
    del "%%f"
)