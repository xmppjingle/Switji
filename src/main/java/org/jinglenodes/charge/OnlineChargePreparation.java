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

package org.jinglenodes.charge;

import org.apache.log4j.Logger;
import org.jinglenodes.callkiller.CallKiller;
import org.jinglenodes.jingle.Reason;
import org.jinglenodes.prepare.CallPreparation;
import org.jinglenodes.prepare.PrepareStatesManager;
import org.jinglenodes.session.CallSession;
import org.jinglenodes.session.CallSessionMapper;
import org.xmpp.component.IqRequest;
import org.xmpp.component.ResultReceiver;
import org.xmpp.component.ServiceException;
import org.xmpp.packet.JID;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.JIDFactory;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipChannel;
import org.zoolu.tools.ConcurrentTimelineHashMap;
import org.zoolu.tools.NamingThreadFactory;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Online Charge Preparation
 *
 * @author bhlangonijr
 */

public class OnlineChargePreparation extends CallPreparation implements ResultReceiver {

    final Logger log = Logger.getLogger(OnlineChargePreparation.class);
    private static final int DEFAULT_THREAD_POOL_SIZE = 10;
    private OnlineChargeServiceProcessor onlineChargeServiceProcessor;
    private CallKiller callKiller;
    private CallSessionMapper sessionMapper;
    private PrepareStatesManager prepareStatesManager;
    private int maxInboundCallDuration = 5400;  // 90 min
    private int maxOutboundCallDuration = 1800;  // 30 min
    private ScheduledThreadPoolExecutor timerExecutor;
    private int threadPoolSize;
    private ConcurrentTimelineHashMap<String, Future> executorTasks;

    public OnlineChargePreparation(int threadPoolSize) {
        this.threadPoolSize = threadPoolSize;
        this.timerExecutor = new ScheduledThreadPoolExecutor(threadPoolSize,
                new NamingThreadFactory("Online Charger Thread"));
        this.executorTasks = new ConcurrentTimelineHashMap<String, Future>(10000, 86400);
    }

    public OnlineChargePreparation() {
        this(DEFAULT_THREAD_POOL_SIZE);
    }

    @Override
    public boolean prepareInitiate(JingleIQ iq, CallSession session) {
        return true;
    }

    @Override
    public boolean proceedInitiate(JingleIQ iq, CallSession session) {
        return true;
    }

    @Override
    public boolean proceedTerminate(JingleIQ iq, CallSession session) {
        log.debug("Online charge Proceed Terminate: " + iq.toXML() + " - " + session.getId());
        setSessionFinishTime(session, System.currentTimeMillis());
        stopCharging(iq, session);
        if (callKiller != null) {
            callKiller.cancelKill(session);
        }
        return true;
    }

    private void chargeCall(JingleIQ iq, CallSession session) {

        if (session.getOnlineChargeSession() == null) {
            final OnlineChargeSession chargeSession = new OnlineChargeSession(
                    null, null, System.currentTimeMillis());
            session.setOnlineChargeSession(chargeSession);
        }

        JID initiator = JIDFactory.getInstance().getJID(session.getOnlineChargeSession().getInitiator() != null ?
                session.getOnlineChargeSession().getInitiator() : iq.getJingle().getInitiator());
        JID responder = JIDFactory.getInstance().getJID(session.getOnlineChargeSession().getResponder() != null ?
                session.getOnlineChargeSession().getResponder() : iq.getJingle().getResponder());

        if (initiator != null && responder != null) {
            if (onlineChargeServiceProcessor != null) {
                try {
                    onlineChargeServiceProcessor.queryService(iq, initiator.getNode(), responder.getNode(), this);
                } catch (ServiceException e) {
                    log.error("Could NOT Query Online Charge Service.", e);
                    if (session != null) {
                        log.error("Forcing stop charging: " + session.getId());
                        stopCharging(iq, session);
                    } else {
                        log.warn("Couldn't stop online charge request - null session ");
                    }
                }
            } else {
                log.error("Charge Error: Online Charge Service Processor is null");
            }
        } else {
            log.error("Charge Error: Could NOT Retrieve Call Info");
        }

    }

    @Override
    public boolean proceedAccept(JingleIQ iq, CallSession session) {
        setSessionStartTime(session, System.currentTimeMillis());
        scheduleKill(session);
        startCharging(iq, session);
        return true;
    }

    @Override
    public void proceedInfo(JingleIQ iq, CallSession session) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    private void setSessionStartTime(final CallSession session, final long time) {
        if (session != null) {
            session.setStartTime(time);
        }
    }

    private void setSessionFinishTime(final CallSession session, final long time) {
        if (session != null) {
            session.setFinishTime(time);
        }
    }

    @Override
    public boolean prepareInitiate(Message msg, CallSession session, final SipChannel sipChannel) {
        return true;
    }

    @Override
    public JingleIQ proceedSIPInitiate(JingleIQ iq, CallSession session, SipChannel channel) {
        return iq;
    }

    @Override
    public void proceedSIPInfo(JingleIQ iq, CallSession session, SipChannel channel) {

    }

    @Override
    public JingleIQ proceedSIPEarlyMedia(JingleIQ iq, CallSession session, SipChannel channel) {
        return iq;
    }

    @Override
    public JingleIQ proceedSIPTerminate(JingleIQ iq, CallSession session, SipChannel channel) {
        setSessionFinishTime(session, System.currentTimeMillis());
        stopCharging(iq, session);
        if (callKiller != null) {
            callKiller.cancelKill(session);
        }
        return iq;
    }

    @Override
    public JingleIQ proceedSIPAccept(JingleIQ iq, CallSession session, SipChannel channel) {
        setSessionStartTime(session, System.currentTimeMillis());
        scheduleKill(session);
        startCharging(iq, session);
        return iq;
    }

    public OnlineChargeServiceProcessor getOnlineChargeServiceProcessor() {
        return onlineChargeServiceProcessor;
    }

    public void setOnlineChargeServiceProcessor(OnlineChargeServiceProcessor chargeServiceProcessor) {
        log.debug("Added Charge Service Processor");
        this.onlineChargeServiceProcessor = chargeServiceProcessor;
    }

    @Override
    public void receivedResult(IqRequest iq) {
        if (log.isDebugEnabled()) {
            log.debug("Online Call Charging: Result: " + iq.getResult() + " - \nRequest");
        }
    }

    @Override
    public void receivedError(IqRequest iqRequest) {
        log.error("Failed to charge, cancelling call: " + iqRequest.getRequest());
        if (iqRequest.getOriginalPacket() instanceof JingleIQ) {
            final JingleIQ iq = (JingleIQ) iqRequest.getOriginalPacket();
            terminateCall(iq, new Reason("Couldn't charge account", Reason.Type.payment));
        }

    }

    @Override
    public void timeoutRequest(IqRequest iqRequest) {
        log.error("Timeout to charge, cancelling call: " + iqRequest.getRequest());
        if (iqRequest.getOriginalPacket() instanceof JingleIQ) {
            final JingleIQ iq = (JingleIQ) iqRequest.getOriginalPacket();
            terminateCall(iq, new Reason("Timeout", Reason.Type.expired));
        }
    }

    private void terminateCall(JingleIQ iq, Reason reason) {
        final CallSession callSession = sessionMapper.getSession(iq);
        if (callSession != null) {
            callKiller.immediateKill(callSession, reason);
        } else {
            log.warn("Session not found for IQ: " + iq.toXML());
        }
    }

    public CallKiller getCallKiller() {
        return callKiller;
    }

    public void setCallKiller(CallKiller callKiller) {
        this.callKiller = callKiller;
    }

    public CallSessionMapper getSessionMapper() {
        return sessionMapper;
    }

    public void setSessionMapper(CallSessionMapper sessionMapper) {
        this.sessionMapper = sessionMapper;
    }

    public PrepareStatesManager getPrepareStatesManager() {
        return prepareStatesManager;
    }

    public void setPrepareStatesManager(PrepareStatesManager prepareStatesManager) {
        this.prepareStatesManager = prepareStatesManager;
    }

    public int getMaxInboundCallDuration() {
        return maxInboundCallDuration;
    }

    public void setMaxInboundCallDuration(int maxInboundCallDuration) {
        this.maxInboundCallDuration = maxInboundCallDuration;
    }

    public int getMaxOutboundCallDuration() {
        return maxOutboundCallDuration;
    }

    public void setMaxOutboundCallDuration(int maxOutboundCallDuration) {
        this.maxOutboundCallDuration = maxOutboundCallDuration;
    }

    private void scheduleKill(CallSession session) {
        if (getCallKiller() != null) {
            if (session.isJingleInitiator()) {
                callKiller.scheduleKill(session, getMaxOutboundCallDuration());
            } else {
                callKiller.scheduleKill(session, getMaxInboundCallDuration());
            }
        }

    }

    private void startCharging(final JingleIQ iq, final CallSession session) {

        if (session != null && session.getOnlineChargeSession() != null) {
            final long delay = session.getOnlineChargeSession().getChargeSeconds();

            Future future = timerExecutor.scheduleWithFixedDelay(new Runnable() {
                @Override
                public void run() {
                    if (session != null && session.isConnected()) {
                        chargeCall(iq, session);
                    } else {
                        throw new RuntimeException("Session is not active");
                    }

                }
            }, 0, delay, TimeUnit.SECONDS);

            executorTasks.put(session.getId(), future);

        }
    }

    private void stopCharging(final JingleIQ iq, final CallSession session) {
        try {
            Future future = executorTasks.remove(session.getId());
            if (future != null && !future.isCancelled()) {
                future.cancel(true);
            }
        } catch (Exception e) {
            log.error("Error cancelling online charge task", e);
        }
    }

}
