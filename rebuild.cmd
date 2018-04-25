call mvn package

rd "C:\openfire_4_2_1\plugins\ofmeet" /q /s
rem rd "C:\openfire_4_2_1\plugins\offocus" /q /s
rd "C:\openfire_4_2_1\plugins\ofswitch" /q /s
rd "C:\openfire_4_2_1\plugins\ofchat" /q /s

del "C:\openfire_4_2_1\plugins\ofmeet.jar" 
rem del "C:\openfire_4_2_1\plugins\offocus.jar" 
del "C:\openfire_4_2_1\plugins\ofswitch.jar" 
del "C:\openfire_4_2_1\plugins\ofchat.jar" 

copy C:\Projects\TL-Open\ofmeet-openfire-plugin\ofmeet\target\ofmeet.jar "C:\openfire_4_2_1\plugins"
rem copy C:\Projects\TL-Open\ofmeet-openfire-plugin\offocus\target\offocus.jar "C:\openfire_4_2_1\plugins"
copy C:\Projects\TL-Open\ofmeet-openfire-plugin\ofswitch\target\ofswitch.jar "C:\openfire_4_2_1\plugins"
copy C:\Projects\TL-Open\ofmeet-openfire-plugin\ofchat\target\ofchat.jar "C:\openfire_4_2_1\plugins"

rd /s /q C:\projects\TL-Open\ofmeet-openfire-plugin\ofchat\classes\jitsi-meet
del "C:\openfire_4_2_1\logs\*.*"
pause