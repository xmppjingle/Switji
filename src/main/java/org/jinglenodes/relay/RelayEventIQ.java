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

public class RelayEventIQ extends IQ {

    public final static String ELEMENT_NAME = "channel";
    public final static String NAMESPACE = "http://jabber.org/protocol/jinglenodes#event";

    public static final String KILLED = "killed";

    private String event;
    private String channelId;
    private String time;
    private boolean isRequest;

    public RelayEventIQ() {
        this(true);
    }

    public RelayEventIQ(final boolean isRequest) {
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

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public Element getChildElement() {

        final Element element = DocumentHelper.createElement(new QName(ELEMENT_NAME, new Namespace("", NAMESPACE)));

        if (event != null) {
            element.addAttribute("event", event);
        }

        if (event != null) {
            element.addAttribute("time", event);
        }

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

    public static RelayEventIQ parseRelayEventIq(final IQ iq) {

        if (Type.set.equals(iq.getType())) {
            final Element e = iq.getChildElement();
            if (e != null && ELEMENT_NAME.equals(e.getName())) {
                final RelayEventIQ r = new RelayEventIQ(false);
                r.setID(iq.getID());
                r.setChannelId(e.attributeValue("id"));
                r.setEvent(e.attributeValue("event"));
                r.setTime(e.attributeValue("time"));
                return r;
            }
        }

        return null;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public static boolean isRelayEventIQ(final IQ iq) {
        if (Type.set.equals(iq.getType()) || Type.error.equals(iq.getType())) {
            final Element e = iq.getChildElement();
            if (e != null && ELEMENT_NAME.equals(e.getName())) {
                return true;
            }
        }
        return false;
    }
}
