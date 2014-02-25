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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.xml.bind.annotation.XmlRootElement;

@XStreamAlias("reason")
@XmlRootElement(name = "reason")
public class Reason {

    @XStreamOmitField
    private Type type;
    private static final String REASON = "reason";
    private static final String CONDITION = "condition";
    private String text;
    /*
   <xs:complexType name='reasonElementType'>
    <xs:sequence>
      <xs:choice>
        <xs:element name='alternative-session' type='alternativeSessionElementType'/>
        <xs:element name='busy' type='empty'/>
        <xs:element name='cancel' type='empty'/>
        <xs:element name='connectivity-error' type='empty'/>
        <xs:element name='decline' type='empty'/>
        <xs:element name='expired' type='empty'/>
        <xs:element name='failed-application' type='empty'/>
        <xs:element name='failed-transport' type='empty'/>
        <xs:element name='general-error' type='empty'/>
        <xs:element name='gone' type='empty'/>
        <xs:element name='incompatible-parameters' type='empty'/>
        <xs:element name='media-error' type='empty'/>
        <xs:element name='security-error' type='empty'/>
        <xs:element name='success' type='empty'/>
        <xs:element name='timeout' type='empty'/>
        <xs:element name='unsupported-applications' type='empty'/>
        <xs:element name='unsupported-transports' type='empty'/>
      </xs:choice>
      <xs:element name='text' type='xs:string' minOccurs='0' maxOccurs='1'/>
      <xs:any namespace='##other' minOccurs='0' maxOccurs='1'/>
    </xs:sequence>
  </xs:complexType>
    */

    public enum Type {
        alternative_session,
        busy,
        cancel,
        connectivity_error,
        decline,
        expired,
        failed_application,
        failed_transport,
        general_error,
        gone,
        incompatible_parameters,
        media_error,
        security_error,
        success,
        timeout,
        unsupported_applications,
        unsupported_transports,
        payment,
        forbidden
    }

//    @XStreamAlias("no-error")
//    public class NoError implements Type{
//    }


    public Reason() {
    }

    public Reason(final Type type) {
        this(null, type);
    }

    public Reason(final String text, final Type type) {
        this.text = text;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(final Type type) {
        this.type = type;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }


    public Element getElement() {
        final Element parent = DocumentHelper.createElement(REASON);
        final Element child = DocumentHelper.createElement(getType().toString().replace('_', '-'));

        parent.add(child);

        if (text != null) {
            final Element text = DocumentHelper.createElement("text");
            parent.add(text);
            text.addText(getText());
        }

        return parent;
    }

    @Override
    public String toString() {
        return super.toString() == null ? null : super.toString().replace('_', '-');
    }
}
