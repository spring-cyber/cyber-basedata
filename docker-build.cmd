@ECHO off

:_set

if %1 == "false" goto _end
echo ============================================
echo ============================================
echo ============================================
set /p v=请输入需要构建的镜像版本号例(0.0.1):
echo ============================================
echo ============================================
echo ============================================

if "%v%" == "" goto _set

:_build
call docker build -t %2:%v% .
call docker push %2:%v%
if errorlevel 1 goto _fail

echo Packaged successfully
echo Packaged path: %2
echo Packaged version: %v%
echo Packing completion time: %date% %time%
goto _end

:_fail
echo Packaging failure

:_end
