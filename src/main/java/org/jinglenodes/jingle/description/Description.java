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

package org.jinglenodes.jingle.description;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XStreamAlias("description")
@XmlRootElement(name = "description")
public class Description {

    @XStreamAsAttribute
    @XStreamAlias("xmlns")
    public final String NAMESPACE = "urn:xmpp:jingle:apps:rtp:1";

    @XStreamAsAttribute
    @XmlAttribute
    private final String media;

    @XStreamImplicit
    @XStreamAlias("payload-type")
    @XmlAttribute
    private final List<Payload> payloads = new ArrayList<Payload>();

    public Description(String media) {
        this.media = media;
    }

    public void addPayload(final Payload payload) {
        payloads.add(payload);
    }

    public void addPayload(List<Payload> payloads) {
        this.payloads.addAll(payloads);
    }

    public List<Payload> getPayloads() {
        return payloads;
    }

    public String getMedia() {
        return media;
    }
}

