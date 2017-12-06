call mvn clean package

rd "C:\openfire_4_1_6\plugins\ofmeet" /q /s
rd "C:\openfire_4_1_6\plugins\offocus" /q /s
rd "C:\openfire_4_1_6\plugins\ofswitch" /q /s
rd "C:\openfire_4_1_6\plugins\ofchat" /q /s

del "C:\openfire_4_1_6\plugins\ofmeet.jar" 
del "C:\openfire_4_1_6\plugins\offocus.jar" 
del "C:\openfire_4_1_6\plugins\ofswitch.jar" 
del "C:\openfire_4_1_6\plugins\ofchat.jar" 

copy C:\Projects\TL-Open\ofmeet-openfire-plugin\ofmeet\target\ofmeet.jar "C:\openfire_4_1_6\plugins"
copy C:\Projects\TL-Open\ofmeet-openfire-plugin\offocus\target\ofocus.jar "C:\openfire_4_1_6\plugins"
copy C:\Projects\TL-Open\ofmeet-openfire-plugin\ofswitch\target\ofswitch.jar "C:\openfire_4_1_6\plugins"
copy C:\Projects\TL-Open\ofmeet-openfire-plugin\ofchat\target\ofchat.jar "C:\openfire_4_1_6\plugins"

rd /s /q C:\projects\TL-Open\ofmeet-openfire-plugin\ofchat\classes\jitsi-meet
del "C:\openfire_4_1_6\logs\*.*"
pause