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

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.tree.BaseElement;
import org.jinglenodes.jingle.content.Content;
import org.jinglenodes.jingle.info.Info;
import org.jinglenodes.jingle.reason.Reason;

import java.util.List;


public class Jingle extends BaseElement {
    private final static String NAME = "jingle";
    private final static String SID = "sid";
    private final static String INITIATOR = "initiator";
    private final static String RESPONDER = "responder";
    private final static String ACTION = "action";

    private Info info;

    public static final String NAMESPACE = "urn:xmpp:jingle:1";

    public enum Action {
        session_initiate, session_terminate, session_accept, content_modify, content_add, session_info, transport_info;

        public String toString() {
            return this.name().replace('_', '-');
        }
    }

    public Jingle(String sid, String initiator, String responder, Action action) {
        super(NAME, Namespace.get(NAMESPACE));
        this.addAttribute(SID, sid);
        this.addAttribute(INITIATOR, initiator);
        this.addAttribute(RESPONDER, responder);
        this.addAttribute(ACTION, action.toString());
    }

    public void setContent(Content content) {
        this.add(content);
    }

    public Content getContent() {
        Element element = this.element("content");
        if (element instanceof Content) {
            return (Content) this.element("content");
        } else {
            return Content.fromElement(element);
        }
    }

    public String getSid() {
        return this.attributeValue(SID);
    }

    public String getInitiator() {
        return this.attributeValue(INITIATOR);
    }

    public String getResponder() {
        return this.attributeValue(RESPONDER);
    }

    public Action getAction() {
        return Action.valueOf(this.attributeValue(ACTION).replace('-', '_'));
    }

    public String toString() {
        return this.asXML();
    }

    public void setInitiator(String initiator) {
        this.addAttribute(INITIATOR, initiator);
    }

    public Reason getReason() {
        Element element = this.element("reason");
        if (element instanceof Reason) {
            return (Reason) this.element("reason");
        } else {
            return Reason.fromElement(element);
        }
    }

    public void setReason(Reason reason) {
        this.add(reason);
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.add(info);
        this.info = info;
    }

    public void setResponder(String responder) {
        this.addAttribute(RESPONDER, responder);
    }

    public Jingle clone() {
        Jingle jingle = (Jingle) super.clone();
        jingle.setInfo(jingle.getInfo());
        return jingle;
    }

    public static Jingle fromElement(Element element) {
        final Jingle jingle;
        if (element instanceof Jingle) {
            jingle = (Jingle) element;
            return (Jingle) jingle.clone();
        }

        if (!element.getName().equals(NAME))
            return null;

        final String sid = element.attributeValue("sid");
        final String initiator = element.attributeValue("initiator");
        final String responder = element.attributeValue("responder");
        final String stringAction = element.attributeValue("action");
        Action action;
        try {
            if (null != stringAction)
                action = Action.valueOf(stringAction.replace('-', '_'));
            else return null;
        } catch (Exception e) {
            return null;
        }

        jingle = new Jingle(sid, initiator, responder, action);
        Element e = element.element("content");
        if (null != e) {
            Content content = Content.fromElement(e);
            if (null != content)
                jingle.setContent(content);
        }
        e = element.element("reason");
        if (null != e) {
            Reason reason = Reason.fromElement(e);
            if (null != reason)
                jingle.setReason(reason);
        }
        List<Element> list = element.elements();
        Info aux = null;
        Info info = null;
        for (Element child : list) {
            aux = Info.fromElement(child);
            if (null != aux) {
                info = aux;
            }
        }
        if (null != info) {
            jingle.setInfo(info);
        }
        return jingle;
    }

}
