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

package org.jinglenodes.relay;

import org.xmpp.component.ExternalComponent;
import org.xmpp.component.NamespaceProcessor;
import org.xmpp.packet.IQ;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 5/23/12
 * Time: 3:21 PM
 */
public class RelayEventProcessor implements NamespaceProcessor {

    private final List<RelayEventListener> listeners = new ArrayList<RelayEventListener>(1);
    protected ExternalComponent component;

    public void init() {
        if (component != null) {
            component.addProcessor(this);
        }
    }

    @Override
    public IQ processIQGet(IQ iq) {
        return null;
    }

    @Override
    public IQ processIQSet(IQ iq) {

        if (RelayEventIQ.isRelayEventIQ(iq)) {
            final RelayEventIQ eventIQ = RelayEventIQ.parseRelayEventIq(iq);

            for (final RelayEventListener l : listeners) {
                l.relayEventReceived(eventIQ);
            }

            return IQ.createResultIQ(iq);
        }

        return null;
    }

    @Override
    public void processIQError(IQ iq) {

    }

    @Override
    public void processIQResult(IQ iq) {

    }

    @Override
    public String getNamespace() {
        return RelayEventIQ.NAMESPACE;
    }

    public void setListener(final RelayEventListener relayEventListener) {
        listeners.add(relayEventListener);
    }

    public ExternalComponent getComponent() {
        return component;
    }

    public void setComponent(ExternalComponent component) {
        this.component = component;
    }
}