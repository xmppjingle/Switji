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

package org.jinglenodes.jingle.info;

import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.tree.BaseElement;

public class Info extends BaseElement {

    public final static String NAMESPACE = "urn:xmpp:jingle:apps:rtp:info:1";
    private final static String NAME = "name";
    private final static String CREATOR = "creator";

    public enum Type {ringing, active, hold, mute, unhold, unmute}

    public enum Creator {initiator, responder}

    public Info(Type type) {
        super(type.name(), Namespace.get(NAMESPACE));
    }

    public Type getType() {
        return Type.valueOf(this.getName());
    }

    public Creator getCreator() {
        if (this.getType().equals(Type.mute) || this.getType().equals(Type.unmute)) {
            return Creator.valueOf(this.attributeValue(CREATOR));
        }
        return null;
    }

    public String getAttributeName() {
        if (this.getType().equals(Type.mute) || this.getType().equals(Type.unmute))
            return this.attributeValue(NAME);
        else
            return null;
    }

    public static Info fromElement(Element element) {
        final Info info;
        if (element instanceof Info) {
            info = (Info) element;
            return (Info) info.clone();
        }
        Type type;
        try {
            type = Type.valueOf(element.getName());
        } catch (Exception e) {
            return null;
        }

        info = new Info(type);
        if (type.equals(Type.mute) || type.equals(Type.unmute)) {
            info.addAttribute(NAME, element.attributeValue(NAME));
            info.addAttribute(CREATOR, element.attributeValue(CREATOR));
        }
        return info;
    }
}

