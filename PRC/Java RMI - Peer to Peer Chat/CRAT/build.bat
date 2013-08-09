set CREATEFOLDER=CRATClient
set jarFile=cratClient.jar
set securityPolicy=security.policy
set RunnerScript=CratClientRunner.bat
set mainMethod=crat.client.MainClient
set packageCommon=src\crat\common
set packageParticular=src\crat\client

call:createEverything

set CREATEFOLDER=CRATServer
set jarFile=cratServer.jar
set securityPolicy=security.policy
set RunnerScript=CratServerRunner.bat
set mainMethod=crat.server.MainServer
set packageCommon=src\crat\common
set packageParticular=src\crat\server
call:createEverything

exit
::-------------------------------------------

:createEverything
rmdir %CREATEFOLDER% /s /q
mkdir %CREATEFOLDER%

echo java -classpath %%CD%%\%jarFile% -Djava.security.policy=%%CD%%\%jarFile%\%securityPolicy% -Djava.rmi.server.codebase=file:/%%CD%%\%jarFile% %mainMethod%>>%RunnerScript%

echo grant {>>%securityPolicy%
echo permission java.security.AllPermission;>>%securityPolicy%
echo };>>%securityPolicy%

javac -d ./ %packageCommon%\*.java
javac -d ./ %packageParticular%\*.java
jar cf0 %jarFile% %securityPolicy% crat\*

rmdir crat /s /q

move %jarFile% %CREATEFOLDER%
move %RunnerScript% %CREATEFOLDER%
del %securityPolicy%
goto:eof



