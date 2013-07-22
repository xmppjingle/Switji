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

import com.thoughtworks.xstream.annotations.XStreamOmitField;
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
public class CallSession {

    @XStreamOmitField
    private static final Logger log = Logger.getLogger(CallSession.class);
    private String id;
    private final List<JID> user = new CopyOnWriteArrayList<JID>();
    private Message lastSentRequest;
    private Message lastSentResponse;
    private Message lastReceivedRequest;
    private Message lastReceivedResponse;
    private Message lastMessage;
    private JingleIQ sentJingle;
    private JingleIQ receivedJingle;
    private JingleIQ initiateIQ;
    private JingleIQ acceptIQ;
    @XStreamOmitField
    private ConcurrentLinkedQueue<CallPreparation> preparations;
    @XStreamOmitField
    private ConcurrentLinkedQueue<CallPreparation> proceeds;
    private final Map<String, ContactHeader> userContactBind = (new ConcurrentHashMap<String, ContactHeader>());
    private int retries = 0;
    private long timestamp;
    private long startTime = 0;
    private long finishTime;
    private boolean active = true;
    private AtomicInteger sentRequestsCounter = new AtomicInteger();
    private RelayIQ relayIQ;
    private boolean connected = false;
    private long creationTime;
    private SessionCredit sessionCredit;
    @XStreamOmitField
    private SessionUpdateListener sessionUpdateListener;
    private boolean jingleInitiator;
    private boolean callKilled;
    private JingleIQ forwardInitIq;

    public CallSession(final String id, final JID user) {
        this.id = id;
        this.user.add(user);
        this.timestamp = System.currentTimeMillis();
        this.creationTime = System.currentTimeMillis();
        preparations = new ConcurrentLinkedQueue<CallPreparation>();
        proceeds = new ConcurrentLinkedQueue<CallPreparation>();
    }

    private void update() {
        timestamp = System.currentTimeMillis();
        if (sessionUpdateListener != null) {
            sessionUpdateListener.sessionUpdated(this);
        }
    }

    public void addSentRequest(final Message request) {
        lastMessage = request;
        lastSentRequest = request;
        sentRequestsCounter.incrementAndGet();
        update();
    }

    public void addReceivedRequest(final Message request) {
        lastMessage = request;
        lastReceivedRequest = request;
        update();
    }

    public int getSentRequests() {
        return sentRequestsCounter.get();
    }

    public void resetSentRequestsCounter() {
        sentRequestsCounter.set(0);
    }

    public void addSentResponse(final Message response) {
        lastMessage = response;
        lastSentResponse = response;
        update();
    }

    public void addReceivedResponse(final Message response) {
        setRetries(0);
        lastMessage = response;
        lastReceivedResponse = response;
        update();
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
        update();
    }

    public void addSentJingle(final JingleIQ iq) {
        sentJingle = iq;
        update();
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

    public JingleIQ getAcceptIQ() {
        return acceptIQ;
    }

    public void setAcceptIQ(JingleIQ acceptIQ) {
        this.acceptIQ = acceptIQ;
    }

    public void deactivate() {
        active = false;
    }

    public void destroy() {
        userContactBind.clear();
        active = false;
        if (sessionUpdateListener != null) {
            sessionUpdateListener.sessionDestroyed(this);
        }
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

    public SessionUpdateListener getSessionUpdateListener() {
        return sessionUpdateListener;
    }

    public void setSessionUpdateListener(SessionUpdateListener sessionUpdateListener) {
        this.sessionUpdateListener = sessionUpdateListener;
    }

    public void setProceeds(ConcurrentLinkedQueue<CallPreparation> proceeds) {
        this.proceeds = proceeds;
    }

    public void setPreparations(ConcurrentLinkedQueue<CallPreparation> preparations) {
        this.preparations = preparations;
    }

    public ConcurrentLinkedQueue<CallPreparation> getPreparations() {
        return preparations;
    }

    public long getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(long finishTime) {
        this.finishTime = finishTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public boolean isJingleInitiator() {
        return jingleInitiator;
    }

    public void setJingleInitiator(boolean jingleInitiator) {
        this.jingleInitiator = jingleInitiator;
    }

    public boolean isCallKilled() {
        return callKilled;
    }

    public void setCallKilled(boolean callKilled) {
        this.callKilled = callKilled;
    }

    public JingleIQ getForwardInitIq() {
        return forwardInitIq;
    }

    public void setForwardInitIq(JingleIQ forwardInitIq) {
        this.forwardInitIq = forwardInitIq;
    }

    @Override
    public String toString() {
        return "CallSession{" +
                "id='" + id + '\'' +
                ", user=" + user +
                ", lastSentRequest=" + lastSentRequest +
                ", lastSentResponse=" + lastSentResponse +
                ", lastReceivedRequest=" + lastReceivedRequest +
                ", lastReceivedResponse=" + lastReceivedResponse +
                ", lastMessage=" + lastMessage +
                ", sentJingle=" + sentJingle +
                ", receivedJingle=" + receivedJingle +
                ", initiateIQ=" + initiateIQ +
                ", acceptIQ=" + acceptIQ +
                ", preparations=" + preparations +
                ", proceeds=" + proceeds +
                ", userContactBind=" + userContactBind +
                ", retries=" + retries +
                ", timestamp=" + timestamp +
                ", startTime=" + startTime +
                ", finishTime=" + finishTime +
                ", active=" + active +
                ", sentRequestsCounter=" + sentRequestsCounter +
                ", relayIQ=" + relayIQ +
                ", connected=" + connected +
                ", creationTime=" + creationTime +
                ", sessionCredit=" + sessionCredit +
                ", sessionUpdateListener=" + sessionUpdateListener +
                ", jingleInitiator=" + jingleInitiator +
                ", callKilled=" + callKilled +
                ", forwardInitIq=" + forwardInitIq +
                '}';
    }
}