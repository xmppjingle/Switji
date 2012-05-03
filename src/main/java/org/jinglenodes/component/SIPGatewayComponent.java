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

package org.jinglenodes.component;

import org.apache.log4j.Logger;
import org.jinglenodes.jingle.processor.JingleProcessor;
import org.jinglenodes.sip.GatewayRouter;
import org.jinglenodes.sip.processor.SipProcessor;
import org.jinglenodes.sip.router.GatewaySipRouter;
import org.xmpp.component.ExternalComponent;
import org.xmpp.packet.JID;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipChannel;

public class SIPGatewayComponent extends ExternalComponent implements GatewayRouter {
    final Logger log = Logger.getLogger(SIPGatewayComponent.class);
    private String serverDomain;
    private JingleProcessor jingleProcessor;
    private SipProcessor sipProcessor;
    private GatewaySipRouter gatewaySipRouter;

    /**
     * Create a new component which provides weather information.
     *
     * @param name         The name of this component.
     * @param description  The name of this component.
     * @param serverDomain The XMPP domain to which this component is registered to.
     */
    public SIPGatewayComponent(final String name, final String description, String serverDomain) {
        super(name, description, serverDomain);
    }

    public JingleProcessor getJingleProcessor() {
        return jingleProcessor;
    }

    public void setJingleProcessor(JingleProcessor jingleProcessor) {
        this.jingleProcessor = jingleProcessor;
    }

    public void routeSIP(Message invite, JID from) {
        gatewaySipRouter.routeSIP(invite, from);
    }

    public SipChannel getSipChannel(String s) {
        return gatewaySipRouter.getSipChannel(s);
    }

    public GatewaySipRouter getGatewaySipRouter() {
        return gatewaySipRouter;
    }

    public void setGatewaySipRouter(GatewaySipRouter gatewaySipRouter) {
        this.gatewaySipRouter = gatewaySipRouter;
    }

    public SipProcessor getSipProcessor() {
        return sipProcessor;
    }

    public void setSipProcessor(SipProcessor sipProcessor) {
        this.sipProcessor = sipProcessor;
    }

    public void setServerDomain(String serverDomain) {
        this.serverDomain = serverDomain;
    }

    public void init() {
        gatewaySipRouter.addPacketProcessor(sipProcessor);
        this.addProcessor(jingleProcessor);

    }
}
