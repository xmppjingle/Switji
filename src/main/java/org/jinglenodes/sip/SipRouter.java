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

package org.jinglenodes.sip;


import org.jinglenodes.sip.router.SipRoutingListener;
import org.xmpp.packet.JID;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipChannel;
import org.zoolu.sip.provider.SipProviderInfoInterface;

import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Collection;

public interface SipRouter {

    public void routeSIP(Message message, JID sender);

    public void handlePacketReceived(ByteBuffer byteBuffer, SocketAddress address, SipChannel channel);

    public SipProviderInfoInterface getSipProvider();

    public void addRoutingListener(SipRoutingListener routingListener);

    public void removeRoutingListener(SipRoutingListener routingListener);

    public void addPacketProcessor(SipPacketProcessor sipPacketProcessor);

    public void removePacketProcessor(SipPacketProcessor sipPacketProcessor);

    public SipChannel getSipChannel(String id);

    public void destroyChannel(SipChannel channel);

    public Collection<SipChannel> getKeepAliveTargets();

    public void shutdown();

}
