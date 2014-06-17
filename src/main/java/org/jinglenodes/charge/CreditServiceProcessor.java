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

package org.jinglenodes.charge;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.jinglenodes.prepare.NodeFormat;
import org.jinglenodes.session.CallSession;
import org.jinglenodes.session.CallSessionMapper;
import org.xmpp.component.AbstractServiceProcessor;
import org.xmpp.component.IqRequest;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.JIDFactory;

import java.util.IllegalFormatException;

/**
 * Created by IntelliJ IDEA.
 * Changed by benhur: OCS
 * User: thiago
 * Date: 3/26/12
 * Time: 1:54 PM
 */
public class CreditServiceProcessor extends AbstractServiceProcessor {
    private final Logger log = Logger.getLogger(CreditServiceProcessor.class);
    private static final int DEFAULT_BILLING_TIME_SLICE = 60;
    private final Element requestElement;
    private final String xmlns;
    private CallSessionMapper sessionMapper;
    private String creditService;
    private NodeFormat nodeFormat;
    private int chargeSeconds=DEFAULT_BILLING_TIME_SLICE ;

    public CreditServiceProcessor(final String elementName, final String xmlns) {
        this.xmlns = xmlns;
        this.requestElement = DocumentHelper.createElement(new QName(elementName, new Namespace("", xmlns)));
    }

    @Override
    public IQ createServiceRequest(Object object, String fromNode, String toNode) {
        if (object instanceof JingleIQ) {
            final IQ request = new IQ(IQ.Type.set);
            toNode = nodeFormat.formatNode(toNode, fromNode);
            final JID to = JIDFactory.getInstance().getJID(null, creditService, null);
            final JID from = JIDFactory.getInstance().getJID(fromNode, this.getComponentJID().getDomain(), null);
            final JingleIQ jingleIQ = (JingleIQ) object;
            request.setTo(to);
            request.setFrom(from);
            request.setChildElement(requestElement.createCopy());
            final String toBareJid = JIDFactory.getInstance().getJID(toNode, creditService, null).toBareJID();

            OnlineChargeSession sessionCredit = new OnlineChargeSession(OnlineChargeSession.RouteType.pstn,
                    getChargeSeconds(), from.toBareJID(), toBareJid, System.currentTimeMillis());

            final CallSession session = sessionMapper.getSession(jingleIQ);
            if (session != null) {
                session.setOnlineChargeSession(sessionCredit);
            } else {
                log.warn("Session not found for jingleIQ: " + jingleIQ.toXML());
            }

            final Element e = request.getChildElement();
            e.addAttribute("initiator", sessionCredit.getInitiator());
            e.addAttribute("responder", sessionCredit.getResponder());
            e.addAttribute("sid", jingleIQ.getJingle().getSid());
            log.debug("createConsumeBeginRequest: " + request.toXML());

            return request;
        }
        return null;
    }

    @Override
    protected String getRequestId(Object obj) {
        if (obj instanceof JingleIQ) {
            final JingleIQ iq = (JingleIQ) obj;
            return iq.getJingle().getSid();
        }
        return null;
    }

    @Override
    protected void handleResult(IqRequest iq) {
        if (iq.getOriginalPacket() instanceof JingleIQ) {
            log.debug("Credit Value Received: " + iq.getResult().toXML());
            final CallSession session = sessionMapper.getSession((JingleIQ) iq.getOriginalPacket());
            if (session != null) {
                getSessionCredit(iq.getResult(), session.getOnlineChargeSession());
            }
        }
    }

    /*
      * Create the SessionCredit
      * the information retrieved from the iq
      */
    protected OnlineChargeSession getSessionCredit(final IQ iq, final OnlineChargeSession sessionCredit) {
        String type = null;
        String seqNumber = null;

        log.debug("Get Consume-begin response: " + iq.toXML());

        final Element e = iq.getChildElement();
        type = e.attributeValue("type");
        Element esPrivate = e.element("es-private");
        if (esPrivate != null) {
            seqNumber = esPrivate.attributeValue("seqnr");
            if (log.isDebugEnabled()) {
                log.debug("Sequence " + seqNumber + "number for IQ " + iq.toXML() );
            }
        }

        if (type != null) {
            try {

                final OnlineChargeSession.RouteType rt = OnlineChargeSession.RouteType.valueOf(type);
                sessionCredit.setRouteType(rt);
                sessionCredit.setSeqNumber(seqNumber);

            } catch (IllegalFormatException ife) {
                log.error("Invalid Credit Value Received: " + iq.toXML(), ife);
            } catch (IllegalArgumentException ife) {
                log.error("Invalid Route Type Value Received: " + iq.toXML(), ife);
            }
        }

        return sessionCredit;
    }

    @Override
    protected void handleError(IqRequest iqRequest) {
        log.error("Failed to Start Online Charging: " + iqRequest.getResult().toXML());

        if (iqRequest.getOriginalPacket() instanceof JingleIQ) {
            final CallSession session = sessionMapper.getSession((JingleIQ) iqRequest.getOriginalPacket());
            if (session != null && session.getOnlineChargeSession() != null) {
                log.warn("Forcing Call Consume-begin for: " + iqRequest.getResult().toXML());
                final OnlineChargeSession sessionCredit =
                        new OnlineChargeSession(null, null, System.currentTimeMillis());
                session.setOnlineChargeSession(sessionCredit);
            }
        }
    }

    @Override
    protected void handleTimeout(IqRequest request) {

    }

    @Override
    public String getNamespace() {
        return xmlns;
    }

    public String getCreditService() {
        return creditService;
    }

    public void setCreditService(String creditService) {
        this.creditService = creditService;
    }

    public CallSessionMapper getSessionMapper() {
        return sessionMapper;
    }

    public void setSessionMapper(CallSessionMapper sessionMapper) {
        this.sessionMapper = sessionMapper;
    }

    public NodeFormat getNodeFormat() {
        return nodeFormat;
    }

    public void setNodeFormat(NodeFormat nodeFormat) {
        this.nodeFormat = nodeFormat;
    }

    public int getChargeSeconds() {
        return chargeSeconds;
    }

    public void setChargeSeconds(int chargeSeconds) {
        this.chargeSeconds = chargeSeconds;
    }

}
