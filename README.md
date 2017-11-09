[![Build Status](https://travis-ci.org/traderlynk/ofmeet-openfire-plugin.svg?branch=master)](https://travis-ci.org/traderlynk/ofmeet-openfire-plugin)

Etherlynk Project
============================================

This project produces four Openfire plugins, offocus, ofmeet, ofswitch and ofchat that, combined, provide a WebRTC-based decentralized unified communications solution.

Etherlynk project bundles various third-party products, notably:
- the [Ignite Realtime](https://github.com/igniterealtime/openfire) project
- the [Jitsi Videobridge](https://github.com/jitsi/jitsi-videobridge) project;
- the [Jitsi Conference Focus (jicofo)](https://github.com/jitsi/jicofo) project; 
- the [FreeSWITCH](https://freeswitch.org) project.


Installation
------------
Install the offocus, ofmeet, ofswitch and ofchat plugins into your Openfire instance.

Build instructions
------------------

This project is a Apache Maven project, and is build using the standard Maven invocation:

    mvn clean package

After a successful execution, the four plugins should be available in these locations:

    offocus/target/offocus.jar
    ofmeet/target/ofmeet.jar
    ofswitch/target/ofswitch.jar
    ofchat/target/ofchat.jar    

TODO - Tidy up all these bits of stuff
---------------------------------------

Example modification to FreeSWITCH public dialplan
````
<extension name="ofmeet">
  <condition field="${module_exists(mod_enum)}" expression="true"/>
  <condition field="destination_number" expression="^(.*)$">
<action application="conference" data="$1"/>
  </condition>
</extension>  
````    