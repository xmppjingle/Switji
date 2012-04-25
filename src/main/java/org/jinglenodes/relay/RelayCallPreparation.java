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

import org.apache.log4j.Logger;
import org.jinglenodes.jingle.processor.JingleProcessor;
import org.jinglenodes.prepare.CallPreparation;
import org.jinglenodes.prepare.PrepareStatesManager;
import org.jinglenodes.session.CallSession;
import org.xmpp.component.IqRequest;
import org.xmpp.component.ResultReceiver;
import org.xmpp.component.ServiceException;
import org.xmpp.packet.JID;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.JIDFactory;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipChannel;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 3/19/12
 * Time: 5:21 PM
 */
public class RelayCallPreparation extends CallPreparation implements ResultReceiver {

    final Logger log = Logger.getLogger(RelayCallPreparation.class);
    private RelayServiceProcessor relayServiceProcessor;
    private PrepareStatesManager prepareStatesManager;

    @Override
    public void receivedResult(final IqRequest iqRequest) {
        if (iqRequest.getOriginalPacket() != null) {
            prepareStatesManager.prepareCall((JingleIQ) iqRequest.getOriginalPacket(), null);
        }
    }

    @Override
    public void receivedError(IqRequest iqRequest) {
    }

    @Override
    public void timeoutRequest(IqRequest iqRequest) {

    }

    @Override
    public boolean prepareInitiate(JingleIQ iq, CallSession session) {
        JID initiator = JIDFactory.getInstance().getJID(iq.getJingle().getInitiator());
        if (session.getRelayIQ() == null) {
            try {
                relayServiceProcessor.queryService(iq, null, initiator.getNode(), this);
            } catch (ServiceException e) {
                log.error("Failed Querying Account Service.", e);
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean proceedInitiate(JingleIQ iq, CallSession session) {
        if (session != null) {
            if (session.getRelayIQ() != null) {
                JingleProcessor.updateJingleTransport(iq, session.getRelayIQ());
            }
        }
        return true;
    }

    @Override
    public boolean proceedTerminate(JingleIQ iq, CallSession session) {
        return true;
    }

    @Override
    public boolean proceedAccept(JingleIQ iq, CallSession session) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean prepareInitiate(Message msg, CallSession session, final SipChannel sipChannel) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean proceedInitiate(Message msg, CallSession session, final SipChannel sipChannel) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean proceedTerminate(Message msg, CallSession session, final SipChannel sipChannel) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean proceedAccept(Message msg, CallSession session, final SipChannel sipChannel) {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public RelayServiceProcessor getRelayServiceProcessor() {
        return relayServiceProcessor;
    }

    public void setRelayServiceProcessor(RelayServiceProcessor relayServiceProcessor) {
        this.relayServiceProcessor = relayServiceProcessor;
    }

    public PrepareStatesManager getPrepareStatesManager() {
        return prepareStatesManager;
    }

    public void setPrepareStatesManager(PrepareStatesManager prepareStatesManager) {
        this.prepareStatesManager = prepareStatesManager;
    }
}
