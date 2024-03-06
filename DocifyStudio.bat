@echo off
setlocal

:: Find the .jar file in the build/libs directory
for %%f in (build/libs/*.jar) do (
    set JARFILE=%%f
)
:: Check if the jar file variable is set and not empty
if defined JARFILE (
    echo Found jar file: %JARFILE%
    java --module-path "lib/javafx-sdk-21.0.1/lib" --add-modules javafx.controls,javafx.fxml,javafx.web --add-opens javafx.web/com.sun.javafx.webkit=ALL-UNNAMED -jar build/libs/%JARFILE%
) else (
    echo No jar file found in build/libs directory.
)

endlocal
