/*
 * Copyright (C) 2011 - Jingle Nodes - Yuilop - Neppo
 *
 *   This file is part of Switji (http://jinglenodes.org)
 *
 *   Switji is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   Switji is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MjSip; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *   Author(s):
 *   Benhur Langoni (bhlangonijr@gmail.com)
 *   Thiago Camargo (barata7@gmail.com)
 */

package org.jinglenodes.sip.account;

import org.zoolu.sip.provider.SipProviderInfoInterface;

/**
 * SipAccount instance. This class handle all SIP account information for a user
 *
 * @author Thiago Rocha Camargo
 */
public class SipAccount {

    private String sipUsername = "";

    private String authUsername = "";

    private String displayName = "";

    private String password = "";

    private String server = "";

    private String outboundproxy = "";

    private SipProviderInfoInterface sipProvider = null;

    public SipAccount(final String sipUsername, final String authUsername, final String displayName, final String password, final String server, final String outboundproxy) {
        this.sipUsername = sipUsername;
        this.authUsername = authUsername;
        this.displayName = displayName;
        this.password = password;
        this.server = server;
        if (this.server != null) {
            this.server = this.server.trim();
        }
        this.outboundproxy = outboundproxy;
    }

    public String getAuthUsername() {
        return authUsername == null ? "" : authUsername;
    }

    public void setAuthUsername(final String authUsername) {
        this.authUsername = authUsername;
    }

    public String getDisplayName() {
        return displayName == null ? "" : displayName;
    }

    public void setDisplayName(final String displayName) {
        this.displayName = displayName;
    }

    public String getServer() {
        return server;
    }

    public void setServer(final String server) {
        this.server = server;
    }

    public String getOutboundproxy() {
        if (outboundproxy == null) {
            return getServer();
        }
        return outboundproxy;
    }

    public void setOutboundproxy(final String outboundproxy) {
        this.outboundproxy = outboundproxy;
    }

    public String getSipUsername() {
        return sipUsername == null ? "" : sipUsername;
    }

    public void setSipUsername(final String sipUsername) {
        this.sipUsername = sipUsername;
    }

    public String getPassword() {
        return password == null ? "" : password;
    }

    public void setPassword(final String password) {
        this.password = password;
    }

    public SipProviderInfoInterface getSipProvider() {
        return sipProvider;
    }

    public void setSipProvider(SipProviderInfoInterface sipProvider) {
        this.sipProvider = sipProvider;
    }

}
