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

package org.jinglenodes.jingle.content;

import org.dom4j.tree.BaseElement;
import org.jinglenodes.jingle.description.Description;
import org.jinglenodes.jingle.transport.RawUdpTransport;

public class Content extends BaseElement {
    private final static String ELEMENT_NAME = "content";
    private final static String NAME = "name";
    private final static String CREATOR = "creator";
    private final static String SENDERS = "senders";
    private final static String DISPOSITION = "disposition";

    private Description description;
    private RawUdpTransport transport;

    public enum Senders {
        initiator, none, responder, both
    }

    public enum Creator {
        initiator, responder
    }

    public Content(Creator creator, String name, Senders senders, Description description, RawUdpTransport transport) {
        super(ELEMENT_NAME);
        this.addAttribute(CREATOR, creator.toString());
        this.addAttribute(NAME, name);
        this.addAttribute(SENDERS, senders.toString());
        description.setParent(this);
        transport.setParent(this);
        this.description = description;
        this.transport = transport;
    }

    public String getCreator() {
        return this.attributeValue(CREATOR);
    }

    public String getName() {
        return this.attributeValue(NAME);
    }

    public String getSenders() {
        return this.attributeValue(SENDERS);
    }

    public String getDisposition() {
        return this.attributeValue(DISPOSITION);
    }

    public Description getDescription() {
        return description;
    }

    public RawUdpTransport getTransport() {
        return transport;
    }

    public void setName(String name) {
        this.addAttribute(NAME, name);
    }

    public void setDisposition(String disposition) {
        this.addAttribute(DISPOSITION, disposition);
    }

    public Content clone(){
        Content content = new Content(Creator.valueOf(this.getCreator()), this.getName(), Senders.valueOf(this.getSenders()), this.getDescription(), this.getTransport());
        content.setDisposition(this.getDisposition());
        return content;
    }
}
