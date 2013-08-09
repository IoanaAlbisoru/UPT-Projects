set common_PATH_SOURCE=d:\Workspace\PRC_WORKSPACE\PRC_RMI\Crat_Common\src
set server_PATH_SOURCE=d:\Workspace\PRC_WORKSPACE\PRC_RMI\Crat_Server\src
set client_PATH_SOURCE=d:\Workspace\PRC_WORKSPACE\PRC_RMI\Crat_Client\src

set DEST=d:\Workspace\PRC_WORKSPACE\CRAT\src

xcopy %client_PATH_SOURCE% %DEST% /s /y /i
xcopy %common_PATH_SOURCE% %DEST% /s /y /i
xcopy %server_PATH_SOURCE% %DEST% /s /y /i