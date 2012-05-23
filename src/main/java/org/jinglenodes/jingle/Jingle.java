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


public class Jingle extends BaseElement {

    public final static String SESSION_INITIATE = "session-initiate";
    public final static String SESSION_TERMINATE = "session-terminate";
    public final static String SESSION_ACCEPT = "session-accept";
    public final static String CONTENT_MODIFY = "content-modify";
    public final static String CONTENT_ADD = "content-add";
    public final static String SESSION_INFO = "session-info";
    public final static String TRANSPORT_INFO = "transport-info";

    public enum Action {
        session_initiate, session_terminate, session_accept, content_modify, content_add, session_info, transport_info;

        public String toString() {
            return this.name().replace('_', '-');
        }
    }

    private final static String NAME = "jingle";
    private final static String SID = "sid";
    private final static String INITIATOR = "initiator";
    private final static String RESPONDER = "responder";
    private final static String ACTION = "action";

    public static final String NAMESPACE = "urn:xmpp:jingle:1";

    private Reason reason;
    private Info info;

    public Jingle(String sid, String initiator, String responder, String action) {
        super(NAME, Namespace.get(NAMESPACE));
        this.addAttribute(SID, sid);
        this.addAttribute(INITIATOR, initiator);
        this.addAttribute(RESPONDER, responder);
        this.addAttribute(ACTION, action);
    }

    public void setContent(Content content) {
        this.add(content);
    }

    public Content getContent() {
        Element element = this.element("content");
        if (element instanceof Content){
            return (Content) this.element("content");
        }else{
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

    public String getAction() {
        return this.attributeValue(ACTION);
    }

    public String toString() {
        return this.asXML();
    }

    public void setInitiator(String initiator) {
        this.addAttribute(INITIATOR, initiator);
    }

    public Reason getReason() {
        Element element = this.element("reason");
        if (element instanceof Reason){
            return (Reason) this.element("reason");
        }else{
            return Reason.fromElement(element);
        }
    }

    public void setReason(Reason reason) {
        this.add(reason);
    }

    public Info getInfo() {
        Element element = this.element(null, Namespace.get("urn:xmpp:jingle:apps:rtp:info:1"));
        if (element instanceof Info){
            return (Info) this.element("info");
        }else{
            return Info.fromElement(element);
        }
    }

    public void setInfo(Info info) {
        this.add(info);
    }

    public void setResponder(String responder) {
        this.addAttribute(RESPONDER, responder);
    }

    public static Jingle fromElement(Element element) {
        final String sid = element.attributeValue("sid");
        final String initiator = element.attributeValue("initiator");
        final String responder = element.attributeValue("responder");
        final String action = element.attributeValue("action");
        final Jingle jingle = new Jingle(sid, initiator, responder, action);
        Element e = element.element("content");
        if (null!= e){
            jingle.setContent(Content.fromElement(e));
        }
        e = element.element("reason");
        if (null!= e){
            jingle.setReason(Reason.fromElement(e));
        }
        //TODO get info sub-element
        /*if (null!= e){
            jingle.setInfo(Info.fromElement(e));
        }*/
        return jingle;
    }

    /*
   public Jingle clone() {
       Jingle jingle = new Jingle(this.getSid(), this.getInitiator(), this.getResponder(), this.getAction());
       jingle.setContent(this.getContent());
       jingle.setInfo(this.getInfo());
       jingle.setReason(this.getReason());
       return jingle;
   } */
}
