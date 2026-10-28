@echo off
rem EcoSort - dublu-click pe acest fisier ca sa pornesti aplicatia.
chcp 65001 >nul
powershell -NoProfile -ExecutionPolicy Bypass -File "%~dp0porneste.ps1"
echo.
pause
