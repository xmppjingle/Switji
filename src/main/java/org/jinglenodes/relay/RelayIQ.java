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

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.xmpp.packet.IQ;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 2/14/12
 * Time: 10:13 PM
 * To change this template use File | Settings | File Templates.
 */

public class RelayIQ extends IQ {

    public final static String ELEMENT_NAME = "channel";
    public final static String NAMESPACE = "http://jabber.org/protocol/jinglenodes#channel";
    private String host;
    private String localport;
    private String remoteport;
    private String channelId;
    private String protocol;
    private boolean isRequest;

    public RelayIQ() {
        this(true);
    }

    public RelayIQ(final boolean isRequest) {
        this.isRequest = isRequest;
        if (isRequest) {
            this.setType(IQ.Type.get);
        } else {
            this.setType(IQ.Type.result);
        }
        this.setProtocol("udp");
    }

    public boolean isRequest() {
        return isRequest;
    }

    public void setRequest(boolean request) {
        isRequest = request;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getLocalport() {
        return localport;
    }

    public void setLocalport(String localport) {
        this.localport = localport;
    }

    public String getRemoteport() {
        return remoteport;
    }

    public void setRemoteport(String remoteport) {
        this.remoteport = remoteport;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Element getChildElement() {

        final Element element = DocumentHelper.createElement(new QName(ELEMENT_NAME, new Namespace("", NAMESPACE)));

        if (host != null) {
            element.addAttribute("host", host);
        }

        element.addAttribute("localport", localport);

        element.addAttribute("remoteport", remoteport);

        if (channelId != null) {
            element.addAttribute("id", channelId);
        }
        if (protocol != null) {
            element.addAttribute("protocol", protocol);
        }

        return element;

    }

    public String getChildElementXML() {
        return getChildElement().asXML();
    }

    public String toXML() {
        final Element element = getElement();
        return element.asXML();
    }

    public Element getElement() {
        Element e = super.getElement();
        e = e.createCopy();
        e.add(getChildElement());
        return e;
    }

    public static RelayIQ parseRelayIq(final IQ iq) {

        if (Type.result.equals(iq.getType())) {
            final Element e = iq.getChildElement();
            if (e != null && ELEMENT_NAME.equals(e.getName())) {
                final RelayIQ r = new RelayIQ(false);
                r.setID(iq.getID());
                r.setChannelId(e.attributeValue("id"));
                r.setHost(e.attributeValue("host"));
                r.setLocalport(e.attributeValue("localport"));
                r.setRemoteport(e.attributeValue("remoteport"));
                r.setProtocol(e.attributeValue("protocol"));
                return r;
            }
        }

        return null;
    }

    public static boolean isRelayIQ(final IQ iq) {
        if (Type.result.equals(iq.getType()) || Type.error.equals(iq.getType())) {
            final Element e = iq.getChildElement();
            if (e != null && ELEMENT_NAME.equals(e.getName())) {
                return true;
            }
        }
        return false;
    }
}
