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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.jinglenodes.jingle.description.Description;
import org.jinglenodes.jingle.transport.RawUdpTransport;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XStreamAlias("content")
@XmlRootElement(name = "content")
public class Content {

    @XStreamAsAttribute
    @XmlAttribute
    private String creator, name, senders;

    private Description description;
    private RawUdpTransport transport;

    public Content(String creator, String name, String senders, Description description, RawUdpTransport transport) {
        this.creator = creator;
        this.name = name;
        this.senders = senders;
        this.description = description;
        this.transport = transport;
    }

    public String getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    public String getSenders() {
        return senders;
    }

    public Description getDescription() {
        return description;
    }

    public RawUdpTransport getTransport() {
        return transport;
    }

    public void setName(String name) {
        this.name = name;
    }
}
