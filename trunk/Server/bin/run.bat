@echo off

set classpath=""

for %%f in ( lib\*.jar ) do @call bin\setclasspath %%f

echo %classpath%

java -cp %classpath% -DRoomWareConfig="conf/roomware.conf" org.roomwareproject.server.impl.RoomWareServerImpl
