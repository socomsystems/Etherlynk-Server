call mvn package

rem rd "C:\openfire_4_2_3\plugins\ofmeet" /q /s
rem rd "C:\openfire_4_2_3\plugins\offocus" /q /s
rd "C:\openfire_4_2_3\plugins\ofswitch" /q /s
rd "C:\openfire_4_2_3\plugins\ofchat" /q /s

rem del "C:\openfire_4_2_3\plugins\ofmeet.jar" 
rem del "C:\openfire_4_2_3\plugins\offocus.jar" 
del "C:\openfire_4_2_3\plugins\ofswitch.jar" 
del "C:\openfire_4_2_3\plugins\ofchat.jar" 

rem copy C:\Projects\TL-Open\ofmeet-openfire-plugin\ofmeet\target\ofmeet.jar "C:\openfire_4_2_3\plugins"
rem copy C:\Projects\TL-Open\ofmeet-openfire-plugin\offocus\target\offocus.jar "C:\openfire_4_2_3\plugins"
copy C:\Projects\TL-Open\ofmeet-openfire-plugin\ofswitch\target\ofswitch.jar "C:\openfire_4_2_3\plugins"
copy C:\Projects\TL-Open\ofmeet-openfire-plugin\ofchat\target\ofchat.jar "C:\openfire_4_2_3\plugins"

rd /s /q C:\projects\TL-Open\ofmeet-openfire-plugin\ofchat\classes\jitsi-meet
del "C:\openfire_4_2_3\logs\*.*"
pause