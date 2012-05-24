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

package org.jinglenodes.jingle.reason;

import org.dom4j.Element;
import org.dom4j.tree.BaseElement;

import java.util.List;

public class Reason extends BaseElement {

    private static final String NAME = "reason";
    private static final String TEXT = "text";
    private ReasonType reasonType;

    public Reason(final ReasonType type) {
        this(type, null);
    }

    public Reason(final ReasonType type, final String text) {
        super(NAME);
        this.add(type);
        this.reasonType = type;
        if (null != text)
            this.addElement(TEXT).addCDATA(text);
    }

    public String getText() {
        if (null != this.element(TEXT))
            return this.element(TEXT).getStringValue();
        return null;
    }

    public void setText(final String text) {
        if (null != text)
            this.addElement(TEXT).addCDATA(text);
    }

    public ReasonType getType() {
        return reasonType;
    }

    public Reason clone() {
        return new Reason(this.reasonType, this.getText());
    }

    public static Reason fromElement(Element element) {
        final Reason reason;

        if (element instanceof Reason) {
            reason = (Reason) element;
            return reason.clone();
        }

        if (!element.getName().equals(NAME))
            return null;

        List<Element> list = element.elements();
        if (list.isEmpty()) {
            return null;
        }

        String text = null;
        ReasonType reasonType = null;
        ReasonType aux;
        for (Element child : list) {
            if (child.getName().equals(TEXT)) {
                text = child.elementText(TEXT);
            }
            aux = ReasonType.fromElement(child);
            if (null != aux) {
                reasonType = aux;
            }
        }
        if (null == reasonType)
            return null;
        return new Reason(reasonType, text);
    }
}
