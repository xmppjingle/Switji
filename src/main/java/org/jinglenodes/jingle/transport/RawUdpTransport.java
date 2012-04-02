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

package org.jinglenodes.jingle.transport;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XStreamAlias("transport")
@XmlRootElement(name = "transport")
public class RawUdpTransport {

    @XStreamAsAttribute
    @XStreamAlias("xmlns")
    @XmlAttribute(name = "xmlns")
    public final String NAMESPACE = "urn:xmpp:jingle:transports:raw-udp:1";

    @XStreamImplicit
    @XStreamAlias("candidate")
    @XmlAttribute(name = "candidate")
    private final ArrayList<Candidate> candidates = new ArrayList<Candidate>();

    public RawUdpTransport(Candidate candidate) {
        this.candidates.add(candidate);
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }
}
