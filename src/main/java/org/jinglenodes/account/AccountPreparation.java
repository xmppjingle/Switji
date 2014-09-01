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

package org.jinglenodes.account;

import org.apache.log4j.Logger;
import org.jinglenodes.prepare.CallPreparation;
import org.jinglenodes.prepare.PrepareStatesManager;
import org.jinglenodes.prepare.SipPrepareStatesManager;
import org.jinglenodes.session.CallSession;
import org.jinglenodes.sip.SipToJingleBind;
import org.jinglenodes.sip.account.SipAccount;
import org.xmpp.component.IqRequest;
import org.xmpp.component.ResultReceiver;
import org.xmpp.component.ServiceException;
import org.xmpp.packet.JID;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.JIDFactory;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipChannel;
import org.zoolu.sip.message.SipParsingException;
import org.zoolu.tools.Random;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 3/23/12
 * Time: 2:47 PM
 */
public class AccountPreparation extends CallPreparation implements ResultReceiver {
    final Logger log = Logger.getLogger(AccountPreparation.class);

    private SipToJingleBind sipToJingleBind;
    private PrepareStatesManager prepareStatesManager;
    private SipPrepareStatesManager sipPrepareStatesManager;
    private AccountServiceProcessor accountServiceProcessor;
    private boolean useCachePerSession = true;

    @Override
    public boolean prepareInitiate(JingleIQ iq, final CallSession session) {
        JID initiator = JIDFactory.getInstance().getJID(iq.getJingle().getInitiator());
        if (sipToJingleBind != null) {
            final JID sipFrom = sipToJingleBind.getSipFrom(initiator);
            if (sipFrom != null && !isUseCachePerSession()) {
                return true;
            } else {
                try {
                    accountServiceProcessor.queryService(iq, null, initiator.getNode(), this);
                } catch (ServiceException e) {
                    log.error("Failed Querying Account Service.", e);
                }
            }
        }
        return false;
    }

    @Override
    public boolean proceedInitiate(JingleIQ iq, final CallSession session) {
        JID initiator = JIDFactory.getInstance().getJID(iq.getJingle().getInitiator());
        JID responder = JIDFactory.getInstance().getJID(iq.getJingle().getResponder());
        if (sipToJingleBind != null) {
            final JID sipFrom = sipToJingleBind.getSipFrom(initiator);
            if (sipFrom != null) {
                iq.getJingle().setInitiator(sipFrom.toString());
            }
            final SipAccount account = accountServiceProcessor.getAccountProvider().getSipAccount(initiator);
            if (account != null) {

                try {
                    final List<String> proxies = account.getAlternateOutboundproxies();
                    if (session != null && session.getRetries() > 0 &&
                            proxies != null && proxies.size() > 0) {
                        account.setSipDestinationAddress(proxies.get(Random.nextInt(proxies.size())));
                    }
                } catch (Exception e) {
                    log.error("Error selecting alternative sip proxy", e);
                }

                String [] proxy = account.getSipDestinationAddress().split(":");
                final String resp = responder.getNode() + "@" + proxy[0];
                final String caller = account.getSipUsername() + "@" + initiator.getDomain() +
                        (initiator.getResource() == null ? "" : "/" + initiator.getResource());
                if (log.isDebugEnabled()) {
                    log.debug("Retrieved account: Initiator: " + initiator.toString() + " - new initiator: " + caller);
                }
                iq.getJingle().setResponder(resp);
                iq.getJingle().setInitiator(caller);
            }
        }
        return true;
    }

    @Override
    public void receivedResult(IqRequest iqRequest) {
        if (iqRequest.getOriginalPacket() instanceof JingleIQ) {
            prepareStatesManager.prepareCall((JingleIQ) iqRequest.getOriginalPacket(), null);
        } else if (iqRequest.getOriginalPacket() instanceof Message) {
            log.debug("Account Preparation SIP: " + iqRequest.getRequest().toXML() + " - " + sipPrepareStatesManager);
            sipPrepareStatesManager.prepareCall((Message) iqRequest.getOriginalPacket(), null, null);
        }

    }

    @Override
    public void receivedError(IqRequest iqRequest) {
        log.error("Error Requesting Account");
    }

    @Override
    public void timeoutRequest(IqRequest iqRequest) {
        log.error("Timeout Requesting Account");
    }


    public SipToJingleBind getSipToJingleBind() {
        return sipToJingleBind;
    }

    public void setSipToJingleBind(SipToJingleBind sipToJingleBind) {
        this.sipToJingleBind = sipToJingleBind;
    }

    public PrepareStatesManager getPrepareStatesManager() {
        return prepareStatesManager;
    }

    public void setPrepareStatesManager(PrepareStatesManager prepareStatesManager) {
        this.prepareStatesManager = prepareStatesManager;
    }

    public AccountServiceProcessor getAccountServiceProcessor() {
        return accountServiceProcessor;
    }

    public void setAccountServiceProcessor(AccountServiceProcessor accountServiceProcessor) {
        this.accountServiceProcessor = accountServiceProcessor;
    }

    @Override
    public boolean proceedTerminate(JingleIQ iq, CallSession session) {
        return true;
    }

    @Override
    public boolean proceedAccept(JingleIQ iq, CallSession session) {
        return true;
    }

    @Override
    public void proceedInfo(JingleIQ iq, CallSession session) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean prepareInitiate(Message msg, CallSession session, final SipChannel sipChannel) {
        JID responder = null;
        try {
            responder = msg.getParticipants().getResponder();

            if (sipToJingleBind != null) {
                log.debug("Checking Prepare Initiate: " + responder);
                final JID xmppTo = sipToJingleBind.getXmppTo(responder, null);
                if (xmppTo != null && !isUseCachePerSession()) {
                    log.debug("Found xmppTo for: " + responder + " is: " + xmppTo);
                    return true;
                } else {
                    log.debug("Querying xmppTo for: " + responder);
                    try {
                        accountServiceProcessor.queryService(msg, null, responder.getNode(), this);
                    } catch (ServiceException e) {
                        log.error("Failed Querying Account Service.", e);
                    }
                }
            }
        } catch (SipParsingException e) {
            log.error("Could Not Parse SIP to prepare Initiate", e);
        }
        return false;
    }

    @Override
    public JingleIQ proceedSIPInitiate(JingleIQ iq, CallSession session, SipChannel channel) {
        JID responder = JIDFactory.getInstance().getJID(iq.getJingle().getResponder());
        if (sipToJingleBind != null) {
            log.debug("Account Responder: " + responder);
            final JID xmppTo = sipToJingleBind.getXmppTo(responder, null);
            if (xmppTo != null) {
                iq.setTo(xmppTo);
                iq.setFrom((JID) null);
            } else {
                log.warn("Failed Fetching XmppTo from Account Service.");
            }
        }
        return iq;
    }

    @Override
    public void proceedSIPInfo(JingleIQ iq, CallSession session, SipChannel channel) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JingleIQ proceedSIPEarlyMedia(JingleIQ iq, CallSession session, SipChannel channel) {
        return iq;
    }

    @Override
    public JingleIQ proceedSIPTerminate(JingleIQ iq, CallSession session, SipChannel channel) {
        JID responder = JIDFactory.getInstance().getJID(iq.getJingle().getResponder());
        if (sipToJingleBind != null) {
            log.debug("Account Responder: " + responder);
            final JID xmppTo = sipToJingleBind.getXmppTo(responder, null);
            if (xmppTo != null) {
                iq.setTo(xmppTo);
                iq.setFrom((JID) null);
            } else {
                log.warn("Failed Fetching XmppTo from Account Service.");
            }
        }
        return iq;
    }

    @Override
    public JingleIQ proceedSIPAccept(JingleIQ iq, CallSession session, SipChannel channel) {
        return iq;
    }

    public SipPrepareStatesManager getSipPrepareStatesManager() {
        return sipPrepareStatesManager;
    }

    public void setSipPrepareStatesManager(SipPrepareStatesManager sipPrepareStatesManager) {
        this.sipPrepareStatesManager = sipPrepareStatesManager;
    }

    public boolean isUseCachePerSession() {
        return useCachePerSession;
    }

    public void setUseCachePerSession(boolean useCachePerSession) {
        this.useCachePerSession = useCachePerSession;
    }
}
