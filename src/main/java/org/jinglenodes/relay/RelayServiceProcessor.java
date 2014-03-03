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

package org.jinglenodes.relay;

import org.apache.log4j.Logger;
import org.jinglenodes.jingle.processor.JingleException;
import org.jinglenodes.prepare.ServiceLocator;
import org.jinglenodes.session.CallSession;
import org.jinglenodes.session.CallSessionMapper;
import org.xmpp.component.AbstractServiceProcessor;
import org.xmpp.component.IqRequest;
import org.xmpp.packet.IQ;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.Message;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 2/20/12
 * Time: 8:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class RelayServiceProcessor extends AbstractServiceProcessor {

    final Logger log = Logger.getLogger(RelayServiceProcessor.class);
    private String relayService;
    private final String namespace = RelayIQ.NAMESPACE;
    private CallSessionMapper callSessionMapper;
    private ServiceLocator relayServiceLocator;

    @Override
    public IQ createServiceRequest(Object object, final String fromNode, final String toNode) {
        final RelayIQ relayIQ = new RelayIQ(true);
        final String service = selectRelayService((IQ)object);
        relayIQ.setTo(toNode != null ? toNode + "@" + service : service);
        relayIQ.setFrom(fromNode != null ? fromNode + "@" + this.getComponentJID().getDomain() : null);
        return relayIQ;
    }

    @Override
    protected String getRequestId(Object obj) {
        if (obj instanceof JingleIQ) {
            final JingleIQ iq = (JingleIQ) obj;
            return iq.getJingle().getSid();
        } else if (obj instanceof Message) {
            final Message m = (Message) obj;
            if (m.getCallIdHeader() != null) {
                return m.getCallIdHeader().getCallId();
            }
        }
        return null;
    }

    @Override
    protected void handleResult(IqRequest iq) {
        if (iq.getOriginalPacket() instanceof JingleIQ) {
            final JingleIQ jingleIQ = (JingleIQ) iq.getOriginalPacket();
            final CallSession callSession = callSessionMapper.getSession(jingleIQ);
            if (callSession != null) {
                final RelayIQ relayIQ = RelayIQ.parseRelayIq(iq.getResult());
                callSession.setRelayIQ(relayIQ);
            }
        } else if (iq.getOriginalPacket() instanceof Message) {

            final Message m = (Message) iq.getOriginalPacket();
            final CallSession callSession;
            try {
                callSession = callSessionMapper.getSession(m);
                if (callSession != null) {
                    final RelayIQ relayIQ = RelayIQ.parseRelayIq(iq.getResult());
                    callSession.setRelayIQ(relayIQ);
                }
            } catch (JingleException e) {
                log.error("Could NOT retrieve Call Session to set RelayIQ", e);
            }
        }

    }

    @Override
    protected void handleError(IqRequest iq) {

    }

    @Override
    protected void handleTimeout(IqRequest request) {

    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    public String getRelayService() {
        return relayService;
    }

    public void setRelayService(String relayService) {
        this.relayService = relayService;
    }

    public CallSessionMapper getCallSessionMapper() {
        return callSessionMapper;
    }

    public void setCallSessionMapper(CallSessionMapper callSessionMapper) {
        this.callSessionMapper = callSessionMapper;
    }

    public ServiceLocator getRelayServiceLocator() {
        return relayServiceLocator;
    }

    public void setRelayServiceLocator(ServiceLocator relayServiceLocator) {
        this.relayServiceLocator = relayServiceLocator;
    }

    private String selectRelayService(IQ iqRequest) {
        String service = null;

        if (getRelayServiceLocator() != null) {
            try {
                service = getRelayServiceLocator().getServiceUri(iqRequest);
            } catch (Exception e) {
                log.error("Error while trying to select a relay service, using default["+
                        getRelayService()+"]", e);
            }
        }

        if (service == null) {
            service = getRelayService(); //default
        }

        return service;
    }


}
