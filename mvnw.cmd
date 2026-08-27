@REM Maven Wrapper startup script for Windows
@ECHO OFF
SETLOCAL
SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
IF EXIST "%JAVA_EXE%" GOTO execute
SET JAVA_EXE=java.exe
:execute
"%JAVA_EXE%" -Dmaven.multiModuleProjectDirectory="%CD%" -classpath "%~dp0.mvn\wrapper\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain %*
ENDLOCAL
