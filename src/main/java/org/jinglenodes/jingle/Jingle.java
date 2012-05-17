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

package org.jinglenodes.jingle;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.dom4j.Namespace;
import org.jinglenodes.jingle.content.Content;
import org.xmpp.tinder.parser.XStreamIQ;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XStreamAlias("jingle")
@XmlRootElement(name = "jingle")
public class Jingle {

    public final static String SESSION_INITIATE = "session-initiate";
    public final static String SESSION_TERMINATE = "session-terminate";
    public final static String SESSION_ACCEPT = "session-accept";
    public final static String CONTENT_MODIFY = "content-modify";
    public final static String CONTENT_ADD = "content-add";
    public final static String SESSION_INFO = "session-info";
    public final static String TRANSPORT_INFO = "transport-info";
    public final static String NAME = "jingle";
    public static final Namespace Q_NAMESPACE = new Namespace("", "urn:xmpp:jingle:1");

    @XStreamAsAttribute
    @XStreamAlias("xmlns")
    @XmlAttribute(name = "xmlns")
    public final String NAMESPACE = "urn:xmpp:jingle:1";
    public static final String XMLNS = "urn:xmpp:jingle:1";

    @XStreamAsAttribute
    @XmlAttribute
    private String action, sid, initiator, responder;

    private Content content;
    private Reason reason;
    @XStreamAlias("ringing")
    private Info info;

    public Jingle(String sid, String initiator, String responder, String action) {
        this.sid = sid;
        this.initiator = initiator;
        this.responder = responder;
        this.action = action;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Content getContent() {
        return content;
    }

    public String getSid() {
        return sid;
    }

    public String getInitiator() {
        return initiator;
    }

    public String getResponder() {
        return responder;
    }

    public String getAction() {
        return action;
    }

    public String toString() {
        return XStreamIQ.getStream().toXML(this);
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public Reason getReason() {
        return reason;
    }

    public void setReason(Reason reason) {
        this.reason = reason;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

    public void setResponder(String responder) {
        this.responder = responder;
    }
}
