@echo off

set paf=C:\Users\Julian\Desktop\modding\fabric_litemod\src\main\resources\assets\robot_litemod

for /F "tokens=*" %%A in (stuff.txt) do call :proceror %%A
pause
exit /b

:proceror
echo {"parent":"robot_litemod:block/blendomator_9000/%1"} > %paf%\test\blendomator_9000_%1.json
exit /b
