@echo off

set paf=C:\Users\Julian\Desktop\modding\fabric_litemod\src\main\resources\assets\robot_litemod\models

for /F "tokens=*" %%A in (foods.txt) do call :proceror %%A
pause
exit /b

:proceror
echo {"parent":"item/generated","textures":{"layer0":"robot_litemod:item/food/%1"}} > %paf%\test\%1.json
exit /b
