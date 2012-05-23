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

import org.dom4j.Element;
import org.dom4j.tree.BaseElement;

import java.util.ArrayList;
import java.util.List;

public class Payload extends BaseElement {
    private static final String NAME = "payload-type";
    private static final int CLOCKRATE = 8000;
    private static final int CHANNELS = 1;

    public static final Payload PCMU = new Payload("0", "PCMU");
    public static final Payload PCMA = new Payload("8", "PCMA");
    public static final Payload G729 = new Payload("18", "G729");
    public static final Payload GSM = new Payload("3", "GSM");

    public Payload(String id, String name) {
        super(NAME);
        this.addAttribute("id", id);
        this.addAttribute("name", name);
        this.addAttribute("clockrate", String.valueOf(CLOCKRATE));
        this.addAttribute("channels", String.valueOf(CHANNELS));
    }

    public Payload(String id, String name, int clockrate, int channels) {
        super(NAME);
        this.addAttribute("id", id);
        this.addAttribute("name", name);
        this.addAttribute("clockrate", String.valueOf(clockrate));
        this.addAttribute("channels", String.valueOf(channels));
    }

    public String getId() {
        return this.attributeValue("id");
    }

    public String getName() {
        return this.attributeValue("name");
    }

    public int getClockrate() {
        return Integer.parseInt(this.attributeValue("clockrate"));
    }

    public int getChannels() {
        return Integer.parseInt(this.attributeValue("channels"));
    }

    public void setClockrate(int clockrate) {
        this.addAttribute("clockrate", String.valueOf(clockrate));
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
        }

        return null;

    }

    public Payload clone() {
        return new Payload(this.getId(), this.getName(), this.getClockrate(), this.getChannels());
    }

    public static List<Payload> fromElement(Element element) {
        final List<Payload> payloadList = new ArrayList<Payload>();
        final List<Element> elementList = (List<Element>) element.elements();
        String id, name, clockrate, channels;
        Payload payload;
        for (Element pay : elementList) {
            id = pay.attributeValue("id");
            name = pay.attributeValue("name");
            clockrate = pay.attributeValue("clockrate");
            channels = pay.attributeValue("channels");
            payload = new Payload(id, name);
            if (null != clockrate)
                payload.setClockrate(Integer.parseInt(clockrate));
            if (null != channels)
                payload.setClockrate(Integer.parseInt(channels));
            payloadList.add(payload);
        }
        return payloadList;
    }
}
