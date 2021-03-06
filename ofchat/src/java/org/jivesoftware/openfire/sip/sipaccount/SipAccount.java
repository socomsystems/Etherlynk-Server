/**
 * $RCSfile$
 * $Revision: $
 * $Date: $
 *
 * Copyright (C) 2005-2008 Jive Software. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jivesoftware.openfire.sip.sipaccount;

import org.jivesoftware.util.JiveGlobals;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

/**
 * SipAccount instance. This class handle all SIP account information for a user
 *
 * @author Thiago Rocha Camargo
 */
@XmlRootElement(name = "sipaccount")
@XmlType(propOrder = { "username", "sipUsername", "authUsername", "displayName", "password", "server", "outboundproxy", "stunServer", "stunPort", "voiceMailNumber", "useStun", "enabled", "promptCredentials", "status" })

public class SipAccount
{
    private String username = null;
    private String sipUsername = "";
    private String authUsername = "";
    private String displayName = "";
    private String password = "";
    private String server = "";
    private String outboundproxy = "";
    private String stunServer = "";
    private String stunPort = "";
    private String voiceMailNumber = "";
    private boolean useStun = false;
    private boolean enabled = false;
    private boolean promptCredentials = false;
    private SipRegisterStatus status = SipRegisterStatus.Unregistered;


	public SipAccount() {

	}

    public SipAccount(String username, String sipUsername, String authUsername, String displayName, String password, String server, String outboundproxy, boolean promptCredentials) {
        this.username = username;
        this.sipUsername = sipUsername;
        this.authUsername = authUsername;
        this.displayName = displayName;
        this.password = password;
        this.server = server;
        this.outboundproxy = outboundproxy;
        this.promptCredentials = promptCredentials;
    }

    public SipAccount(String username) {
        this.username = username;
    }


	@XmlElement
    public String getAuthUsername() {
        return authUsername == null ? "" : authUsername;
    }

    public void setAuthUsername(String authUsername) {
        this.authUsername = authUsername;
    }
	@XmlElement
    public String getDisplayName() {
        return displayName == null ? "" : displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

	@XmlElement
    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

	@XmlElement
    public String getVoiceMailNumber() {
        return voiceMailNumber == null ? JiveGlobals.getProperty("phone.voiceMail", "") : voiceMailNumber;
    }

    public void setVoiceMailNumber(String voiceMailNumber) {
        this.voiceMailNumber = voiceMailNumber;
    }

	@XmlElement
    public String getServer() {
        return server == null ? JiveGlobals.getProperty("phone.sipServer", "") : server;
    }

    public void setServer(String server) {
        this.server = server;
    }

	@XmlElement
    public String getOutboundproxy() {
        return outboundproxy;
    }

    public void setOutboundproxy(String outboundproxy) {
        this.outboundproxy = outboundproxy;
    }

	@XmlElement
    public String getSipUsername() {
        return sipUsername == null ? "" : sipUsername;
    }

    public void setSipUsername(String sipUsername) {
        this.sipUsername = sipUsername;
    }

	@XmlElement
    public String getUsername() {
        return username == null ? "" : username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password == null ? "" : password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

	@XmlElement
    public String getStunPort() {
        return stunPort == null ? JiveGlobals.getProperty("phone.stunPort", "") : stunPort;
    }

    public void setStunPort(String stunPort) {
        this.stunPort = stunPort;
    }

	@XmlElement
    public String getStunServer() {
        return stunServer == null ? JiveGlobals.getProperty("phone.stunServer", "") : stunServer;
    }

    public void setStunServer(String stunServer) {
        this.stunServer = stunServer;
    }

	@XmlElement
    public boolean isUseStun() {
        if (stunPort == null && stunServer == null) {
            return JiveGlobals.getBooleanProperty("phone.stunEnabled", false);
        }
        return useStun;
    }

    public void setUseStun(boolean useStun) {
        this.useStun = useStun;
    }

	@XmlElement
    public SipRegisterStatus getStatus() {
        return status == null ? SipRegisterStatus.Unregistered : status;
    }

    public void setStatus(SipRegisterStatus status) {
        this.status = status;
    }

	@XmlElement
    public boolean isPromptCredentials() {
        return promptCredentials;
    }

    public void setPromptCredentials(boolean promptCredentials) {
        this.promptCredentials = promptCredentials;
    }
}
