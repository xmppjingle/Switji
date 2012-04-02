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
import org.jinglenodes.session.CallSession;
import org.jinglenodes.sip.SipToJingleBind;
import org.xmpp.component.IqRequest;
import org.xmpp.component.ResultReceiver;
import org.xmpp.component.ServiceException;
import org.xmpp.packet.JID;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.JIDFactory;

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
    private AccountServiceProcessor accountServiceProcessor;

    @Override
    public boolean prepareInitiate(JingleIQ iq, final CallSession session) {
        JID initiator = JIDFactory.getInstance().getJID(iq.getJingle().getInitiator());
        if (sipToJingleBind != null) {
            final JID sipFrom = sipToJingleBind.getSipFrom(initiator);
            if (sipFrom != null) {
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
        if (sipToJingleBind != null) {
            final JID sipFrom = sipToJingleBind.getSipFrom(initiator);
            if (sipFrom != null) {
                iq.getJingle().setInitiator(sipFrom.toString());
            }
        }
        return true;
    }

    @Override
    public void receivedResult(IqRequest iqRequest) {
        if (iqRequest.getOriginalPacket() instanceof JingleIQ) {
            prepareStatesManager.prepareCall((JingleIQ) iqRequest.getOriginalPacket(), null);
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
}
