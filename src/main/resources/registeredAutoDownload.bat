schtasks /create /sc DAILY /tn AutoDownloadWallpaper /tr %~dp0AutoDownload.bat  /st 15:00 /sd 2018/04/11 /ed 2050/04/11
