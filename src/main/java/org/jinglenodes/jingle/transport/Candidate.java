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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XStreamAlias("candidate")
@XmlRootElement(name = "candidate")
public class Candidate {

    public static final String PEER_REFLEX = "prflx";
    public static final String RELAY = "relay";
    public static final String HOST = "host";

    @XStreamAsAttribute
    @XmlAttribute
    private String ip, port, generation;

    @XStreamAsAttribute
    @XmlAttribute
    private String type;

    public Candidate(String ip, String port, String generation) {
        this.ip = ip;
        this.port = port;
        this.generation = generation;
        this.type = HOST;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getGeneration() {
        return generation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setGeneration(String generation) {
        this.generation = generation;
    }
}
