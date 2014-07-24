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

import org.apache.log4j.Logger;
import org.jinglenodes.sip.SipPacketProcessor;
import org.jinglenodes.sip.SipRouter;
import org.jinglenodes.sip.account.SipAccount;
import org.jinglenodes.sip.account.SipAccountProvider;
import org.xmpp.jnodes.nio.DatagramListener;
import org.xmpp.jnodes.nio.ListenerDatagramChannel;
import org.xmpp.packet.JID;
import org.zoolu.sip.address.SipURL;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipChannel;
import org.zoolu.sip.provider.SipProviderInfoInterface;
import org.zoolu.sip.provider.SipProviderInformation;
import org.zoolu.tools.NamingThreadFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SipTrunkRouter implements SipRouter, DatagramListener {

    static final Logger log = Logger.getLogger(SipTrunkRouter.class);
    private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, new NamingThreadFactory("DynamicMultiPortSipRouterThread"));
    private final List<SipRoutingListener> routingListeners = new ArrayList<SipRoutingListener>();
    private final List<SipPacketProcessor> packetProcessors = new ArrayList<SipPacketProcessor>();
    private final List<SipPacketFilter> packetFilters = new ArrayList<SipPacketFilter>();
    private final SipChannel channel;
    private final Collection<SipChannel> channelsFixed;
    private final String localSipIp;

    // SIP Routing
    private final SipAccountProvider sipAccountProvider;
    private final int keepAliveDelay = 15; // 10 seconds delay in the keep alive packets
    private final SipProviderInfoInterface sipProvider;

    public SipTrunkRouter(final String localSipIp, final int localPort, final SipAccountProvider sipAccountProvider, final String fakeLocalIp, final SocketAddress destination) throws IOException {
        this.localSipIp = localSipIp;
        this.sipAccountProvider = sipAccountProvider;
        this.sipProvider = new SipProviderInformation(fakeLocalIp, localPort);
        channel = new SipChannel("trunk", destination, localSipIp, localPort, this);
        channelsFixed = new ArrayList<SipChannel>();
        channelsFixed.add(channel);
        createSipRouter();
    }

    private void createSipRouter() {
        DynamicKeepAliveTask keepAliveTask = new DynamicKeepAliveTask(this);
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(keepAliveTask, keepAliveDelay, keepAliveDelay, TimeUnit.SECONDS);
    }

    public void destroyChannel(final SipChannel channel) {
        channel.closeDatagramChannel();
    }

    public SipChannel getSipChannel(String id) {
        return channel;
    }

    public void routeSIP(final Message message, final JID sender) {


        for (final SipRoutingListener routingListener : routingListeners) {
            routingListener.routingSIP(message, sender);
        }

        // Route Packet
        final byte[] bytes;
        try {

            final String msg = message.toString();

            bytes = msg.getBytes("UTF-8");
            final ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            final SocketAddress destination = getDestinationAddress(message, sender);

            if (destination == null) {
                log.debug("Could not calculate route for: " + msg);
                return;
            }

            try {

                channel.send(byteBuffer, destination);


                updateKeepAliveTargets(destination);

                for (final SipRoutingListener routingListener : getRoutingListeners()) {
                    routingListener.routedSIP(message, sender);
                }

            } catch (Exception e) {
                log.warn("Unresolved Address.", e);
                for (final SipRoutingListener routingListener : getRoutingListeners()) {
                    routingListener.routingError(message, sender, SipRoutingError.unresolvedAddress);
                }
            }

        } catch (UnsupportedEncodingException e) {
            log.error("Packet Encoding Error.", e);
        }

    }

    SocketAddress getDestinationAddress(final Message message, final JID sender) {

        if (message.getSendTo() != null) {
            return message.getSendTo();
        }

        final SipAccount sipAccount = sipAccountProvider.getSipAccount(sender);
        if (sipAccount != null) {
            final SocketAddress result = CachedAddressResolver.getInstance().getSocketAddress(
                    sipAccount.getSipDestinationAddress());
            message.setSendTo(result);
            return result;
        }

        if (message.getToHeader() != null) {
            final SipURL sipUrl = message.getToHeader().getNameAddress().getAddress();
            final SocketAddress result = CachedAddressResolver.getInstance().getSIPSocketAddress(sipUrl.getHost(),
                    sipUrl.getPort());
            message.setSendTo(result);
            return result;
        }

        return null;

    }

    public SipProviderInfoInterface getSipProvider() {
        return sipProvider;
    }

    public void handlePacketReceived(ByteBuffer byteBuffer, SocketAddress address, final SipChannel channel) {
        for (final SipPacketProcessor packetProcessor : packetProcessors) {
            packetProcessor.processSipPacket(byteBuffer, address, channel);
        }
    }

    public void addPacketFilter(final SipPacketFilter filter) {
        packetFilters.add(filter);
    }

    public void addRoutingListener(final SipRoutingListener routingListener) {
        routingListeners.add(routingListener);
    }

    public void removeRoutingListener(final SipRoutingListener routingListener) {
        routingListeners.remove(routingListener);
    }

    public void addPacketProcessor(final SipPacketProcessor sipPacketProcessor) {
        packetProcessors.add(sipPacketProcessor);
    }

    public void removePacketProcessor(final SipPacketProcessor sipPacketProcessor) {
        packetProcessors.remove(sipPacketProcessor);
    }

    public List<SipRoutingListener> getRoutingListeners() {
        return routingListeners;
    }

    public Collection<SipChannel> getKeepAliveTargets() {
        return channelsFixed;
    }

    void updateKeepAliveTargets(final SocketAddress address) {
    }

    public void shutdown() {
        scheduledThreadPoolExecutor.shutdownNow();
        channel.shutdown();
    }

    public void packetReceived(ByteBuffer byteBuffer, SocketAddress address, SipChannel channel) {

        for (final SipPacketFilter filter : packetFilters) {
            //final boolean r = filter.acceptPacket(byteBuffer, address, channel);
            if (!(filter.acceptPacket(byteBuffer, address, channel))) {
                // Discard Packet
                return;
            }
        }

        handlePacketReceived(byteBuffer, address, channel);
    }

    public void datagramReceived(ListenerDatagramChannel listenerDatagramChannel, ByteBuffer byteBuffer, SocketAddress address) {
        packetReceived(byteBuffer, address, channel);
    }


}