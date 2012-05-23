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

import org.dom4j.tree.BaseElement;

public class Candidate extends BaseElement {
    private static final String NAME = "candidate";
    private static final String COMPONENT = "component";
    private static final String FUNDATION = "fundation";
    private static final String GENERATION = "genetarion";
    private static final String ID = "id";
    private static final String IP = "ip";
    private static final String NETWORK = "network";
    private static final String PORT = "port";
    private static final String PRIORITY = "priority";
    private static final String PROTOCOL = "protocol";
    private static final String TYPE = "type";

    public static final String PEER_REFLEX = "prflx";
    public static final String RELAY = "relay";
    public static final String HOST = "host";

    public Candidate(String ip, String port, String generation) {
        super(NAME);
        this.addAttribute(IP, ip);
        this.addAttribute(PORT, port);
        this.addAttribute(GENERATION, generation);
        this.addAttribute(TYPE, HOST);
    }

    public String getIp() {
        return this.attributeValue(IP);
    }

    public String getPort() {
        return this.attributeValue(PORT);
    }

    public String getGeneration() {
        return this.attributeValue(GENERATION);
    }

    public String getType() {
        return this.attributeValue(TYPE);
    }

    public void setType(String type) {
        this.addAttribute(TYPE, type);
    }

    public void setIp(String ip) {
        this.addAttribute(IP, ip);
    }

    public void setPort(String port) {
        this.addAttribute(PORT, port);
    }

    public void setGeneration(String generation) {
        this.addAttribute(GENERATION, generation);
    }

    public Candidate clone(){
        Candidate candidate = new Candidate(this.getIp(), this.getPort(), this.getGeneration());
        candidate.setType(candidate.getType());
        return candidate;
    }
}
