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

public class RelayRedirectIQ extends IQ {

    public final static String ELEMENT_NAME = "channel";
    public final static String NAMESPACE = "http://jabber.org/protocol/jinglenodes#channelredirect";
    private String host;
    private String port;
    private String channelId;
    private boolean isRequest;

    public RelayRedirectIQ() {
        this(true);
    }

    public RelayRedirectIQ(final boolean isRequest) {
        this.isRequest = isRequest;
        if (isRequest) {
            this.setType(Type.set);
        } else {
            this.setType(Type.result);
        }
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

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Element getChildElement() {

        final Element element = DocumentHelper.createElement(new QName(ELEMENT_NAME, new Namespace("", NAMESPACE)));

        if (host != null) {
            element.addAttribute("host", host);
        }

        element.addAttribute("port", port);

        if (channelId != null) {
            element.addAttribute("id", channelId);
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

    public static RelayRedirectIQ parseRelayIq(final IQ iq) {

        if (Type.result.equals(iq.getType())) {
            final Element e = iq.getChildElement();
            if (e != null && ELEMENT_NAME.equals(e.getName())) {
                final RelayRedirectIQ r = new RelayRedirectIQ(false);
                r.setID(iq.getID());
                r.setChannelId(e.attributeValue("id"));
                r.setHost(e.attributeValue("host"));
                r.setPort(e.attributeValue("localport"));
                return r;
            }
        }

        return null;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public static boolean isRelayRedirectIQ(final IQ iq) {
        if (Type.result.equals(iq.getType()) || Type.error.equals(iq.getType())) {
            final Element e = iq.getChildElement();
            if (e != null && ELEMENT_NAME.equals(e.getName())) {
                return true;
            }
        }
        return false;
    }
}
