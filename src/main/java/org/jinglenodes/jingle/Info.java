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

public class Info extends BaseElement {

    public final static String NAMESPACE = "urn:xmpp:jingle:apps:rtp:info:1";
    private Type type;

    public static Info fromElement(Element element) {
        //TODO
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

    public enum Type {
        ringing, active, hold, mute
    }

    public Info(Type type){
        super(type.toString(), Namespace.get(NAMESPACE));
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Info clone(){
        return new Info(this.getType());
    }
}

