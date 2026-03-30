@echo off
setlocal

set "SCRIPT_DIR=%~dp0"
set "WORKSPACE_DIR=%SCRIPT_DIR%.."
set "ANT_CMD="

where ant >nul 2>nul
if %ERRORLEVEL% EQU 0 (
    set "ANT_CMD=ant"
)

if "%ANT_CMD%"=="" if defined ANT_HOME (
    if exist "%ANT_HOME%\bin\ant.bat" (
        set "ANT_CMD=%ANT_HOME%\bin\ant.bat"
    )
)

if "%ANT_CMD%"=="" (
    for %%P in (
        "C:\sts-4.32.2.RELEASE\plugins\org.apache.ant_1.10.15.v20240901-1000\bin\ant.bat"
        "%USERPROFILE%\.p2\pool\plugins\org.apache.ant_1.10.15.v20240901-1000\bin\ant.bat"
    ) do (
        if exist %%~P (
            set "ANT_CMD=%%~P"
            goto :run
        )
    )
)

:run
if "%ANT_CMD%"=="" (
    echo [ERROR] Apache Ant not found.
    echo [ERROR] Install Ant or set ANT_HOME, then run this script again.
    exit /b 1
)

pushd "%WORKSPACE_DIR%"
call "%ANT_CMD%" -f "deploy\build.xml" %*
set "ANT_EXIT=%ERRORLEVEL%"
popd

exit /b %ANT_EXIT%