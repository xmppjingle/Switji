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

package org.jinglenodes.sip.router;

import org.jinglenodes.util.ConcurrentExpirableHashMap;
import org.zoolu.tools.ConcurrentTimelineHashMap;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class CachedAddressResolver {

    private static final CachedAddressResolver ourInstance = new CachedAddressResolver();
    private long timeout = 3600 * 1000;

    private final ConcurrentExpirableHashMap<String, SocketAddress> cachedAddresses;

    public static CachedAddressResolver getInstance() {
        return ourInstance;
    }

    private CachedAddressResolver() {
        this.cachedAddresses = new ConcurrentExpirableHashMap<String, SocketAddress>(2000,timeout);
    }

    public SocketAddress getSIPSocketAddress(final String address, int port) {

        if (port < 1) {
            port = 5060;
        }

        final String completeAddress = address + port;

        if (cachedAddresses.containsKey(completeAddress)) {
            return cachedAddresses.get(completeAddress);
        }

        final DNSUtil.HostAddress addr = DNSUtil.resolveSIPServerDomain(address, port);

        final SocketAddress socket = new InetSocketAddress(addr.getHost(), addr.getPort());

        cachedAddresses.put(completeAddress, socket);

        return socket;

    }

    public SocketAddress getSocketAddress(final String address) {

        int port = 5060;
        final String[] ipPort = address.split(":");

        if (ipPort.length > 1) {
            try {
                port = Integer.parseInt(ipPort[1]);
            } catch (Exception e) {
                port = 5060;
            }
        }

        return getSIPSocketAddress(ipPort[0], port);

    }

    public void cleanUp() {
        cachedAddresses.clear();
    }

}