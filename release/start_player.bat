@echo off
rem
rem     https://github.com/adyfang/StarPlayer.git
rem
if "%PROCESSOR_ARCHITECTURE%"=="x86" goto x86
if "%PROCESSOR_ARCHITECTURE%"=="AMD64" goto x64

:x86
if "%JAVA_HOME%" == "" goto noJavaHome
echo Please install JDK1.7 at first.
goto noJavaHome
:x64
start /min ./jre7/bin/javaw -Dfile.encoding=utf-8 -jar StarPlayer.jar
goto exit

:noJavaHome
echo The JAVA_HOME environment variable is not defined correctly.
echo NOTE: JAVA_HOME should point to a JDK not a JRE.
echo Start Command: start /min javaw -Dfile.encoding=utf-8 -jar StarPlayer.jar
pause
goto exit

