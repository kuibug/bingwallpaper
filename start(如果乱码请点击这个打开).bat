::if not "%~1"=="p" start /min cmd.exe /c %0 p&exit
echo\ >> ./log/log.txt
echo start %date:~0,10%-%time% >> ./log/log.txt
%~dp0jre/bin/java.exe -jar  -Dfile.encoding=utf-8 wallpaperGUI.jar >> ./log/log.txt