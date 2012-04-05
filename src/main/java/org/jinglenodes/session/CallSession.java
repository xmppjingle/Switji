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

package org.jinglenodes.session;

import org.apache.log4j.Logger;
import org.jinglenodes.credit.SessionCredit;
import org.jinglenodes.prepare.CallPreparation;
import org.jinglenodes.relay.RelayIQ;
import org.xmpp.packet.JID;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.header.ContactHeader;
import org.zoolu.sip.message.Message;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CallSession Stores the Information about the running callSession.
 *
 * @author Thiago Rocha Camargo (thiago@jinglenodes.org) - Jingle Nodes
 */
public abstract class CallSession {

    private static final Logger log = Logger.getLogger(CallSession.class);
    private final List<JID> user = new CopyOnWriteArrayList<JID>();
    private Message lastSentRequest;
    private Message lastSentResponse;
    private Message lastReceivedRequest;
    private Message lastReceivedResponse;
    private JingleIQ sentJingle;
    private JingleIQ receivedJingle;
    private final ConcurrentHashMap<String, String> userHost = new ConcurrentHashMap<String, String>();
    private final ConcurrentLinkedQueue<CallPreparation> preparations = new ConcurrentLinkedQueue<CallPreparation>();
    private final ConcurrentLinkedQueue<CallPreparation> proceeds = new ConcurrentLinkedQueue<CallPreparation>();
    private Message lastMessage;
    private JingleIQ initiateIQ;
    private final Map<String, ContactHeader> userContactBind = (new ConcurrentHashMap<String, ContactHeader>());
    private final String id;
    private int retries = 0;
    private long timestamp;
    private SessionDestroyTask sessionDestroyTask;
    private boolean active = true;
    private AtomicInteger sentRequestsCounter = new AtomicInteger();
    private RelayIQ relayIQ;
    private boolean connected = false;
    private SessionCredit sessionCredit;

    public CallSession(final String id, final JID user) {
        this.id = id;
        this.user.add(user);
        this.timestamp = System.currentTimeMillis();
    }

    public void addSentRequest(final Message request) {
        timestamp = System.currentTimeMillis();
        lastMessage = request;
        lastSentRequest = request;
        sentRequestsCounter.incrementAndGet();
    }

    public void addReceivedRequest(final Message request) {
        timestamp = System.currentTimeMillis();
        lastMessage = request;
        lastReceivedRequest = request;
    }

    public int getSentRequests() {
        return sentRequestsCounter.get();
    }

    public void resetSentRequestsCounter() {
        sentRequestsCounter.set(0);
    }

    public void addSentResponse(final Message response) {
        timestamp = System.currentTimeMillis();
        lastMessage = response;
        lastSentResponse = response;
    }

    public void addReceivedResponse(final Message response) {
        timestamp = System.currentTimeMillis();
        setRetries(0);
        lastMessage = response;
        lastReceivedResponse = response;
    }

    public String getId() {
        return id;
    }

    public Message getLastSentRequest() {
        return lastSentRequest;
    }

    public Message getLastReceivedRequest() {
        return lastReceivedRequest;
    }

    public Message getLastSentResponse() {
        return lastSentResponse;
    }

    public Message getLastReceivedResponse() {
        return lastReceivedResponse;
    }

    public JID getUser() {
        return user.get(this.user.size() - 1);
    }

    public List<JID> getUsers() {
        return user;
    }

    public long getLastTimestamp() {
        return timestamp;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    public JingleIQ getLastSentJingle() {
        return sentJingle;
    }

    public JingleIQ getLastReceivedJingle() {
        return receivedJingle;
    }

    public void addReceivedJingle(final JingleIQ iq) {
        receivedJingle = iq;
    }

    public void addSentJingle(final JingleIQ iq) {
        sentJingle = iq;
    }

    public void setUser(final JID user) {
        if (!this.user.contains(user)) {
            this.user.add(this.user.size(), user);
        }
    }

    public void addContact(final String user, final ContactHeader contactHeader) {
        userContactBind.put(user, contactHeader);
    }

    public ContactHeader getContactHeader(final String user) {
        return userContactBind.get(user);
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(final int retries) {
        this.retries = retries;
    }

    public boolean isActive() {
        return active;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final long timestamp) {
        this.timestamp = timestamp;
    }

    public SessionDestroyTask getSessionDestroyTask() {
        return sessionDestroyTask;
    }

    public void setSessionDestroyTask(final SessionDestroyTask sessionDestroyTask) {
        this.sessionDestroyTask = sessionDestroyTask;
    }

    public ConcurrentHashMap<String, String> getUserHost() {
        return userHost;
    }

    /**
     * @param initiateIQ the initiateIQ to set
     */
    public void setInitiateIQ(JingleIQ initiateIQ) {
        this.initiateIQ = initiateIQ;
    }

    /**
     * @return the initiateIQ
     */
    public JingleIQ getInitiateIQ() {
        return initiateIQ;
    }

    public void deactivate() {
        active = false;
    }

    public void destroy() {

        if (sessionDestroyTask != null) {
            sessionDestroyTask.cancel();
        }
        userContactBind.clear();
        active = false;
    }

    public RelayIQ getRelayIQ() {
        return relayIQ;
    }

    public void setRelayIQ(RelayIQ relayIQ) {
        this.relayIQ = relayIQ;
    }

    public void addCallPreparation(final CallPreparation callPreparation) {
        preparations.add(callPreparation);
    }

    public CallPreparation popCallPreparation() {
        return preparations.poll();
    }

    public void addCallProceed(final CallPreparation callPreparation) {
        proceeds.add(callPreparation);
    }

    public ConcurrentLinkedQueue<CallPreparation> getProceeds() {
        return proceeds;
    }

    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public SessionCredit getSessionCredit() {
        return sessionCredit;
    }

    public void setSessionCredit(SessionCredit sessionCredit) {
        this.sessionCredit = sessionCredit;
    }
}
