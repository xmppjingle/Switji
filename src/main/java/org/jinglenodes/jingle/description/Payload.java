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

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XStreamAlias("payload-type")
@XmlRootElement(name = "payload-type")
public class Payload {

    @XStreamAsAttribute
    @XmlAttribute
    private final String id, name;

    @XStreamAsAttribute
    @XmlAttribute
    private int clockrate = 8000;

    @XStreamAsAttribute
    @XmlAttribute
    private int channels = 1;

    public static final Payload PCMU = new Payload("0", "PCMU");
    public static final Payload PCMA = new Payload("8", "PCMA");
    public static final Payload G729 = new Payload("18", "G729");
    public static final Payload GSM = new Payload("3", "GSM");
    public static final Payload TELEPHONE_EVENT = new Payload("101", "telephone-event");

    public Payload(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Payload(String id, String name, int clockrate, int channels) {
        this.id = id;
        this.name = name;
        this.clockrate = clockrate;
        this.channels = channels;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getClockrate() {
        return clockrate;
    }

    public int getChannels() {
        return channels;
    }

    public void setClockrate(int clockrate) {
        this.clockrate = clockrate;
    }
//
//    public void setChannels(int channels) {
//        this.channels = channels;
//    }

    public static Payload getPayload(final int id) {

        switch (id) {
            case 0:
                return PCMU;
            case 8:
                return PCMA;
            case 18:
                return G729;
            case 3:
                return GSM;
            case 101:
                return TELEPHONE_EVENT;
        }

        return null;

    }
}
