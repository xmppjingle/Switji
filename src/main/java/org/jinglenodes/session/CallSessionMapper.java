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

import org.jinglenodes.jingle.processor.JingleException;
import org.xmpp.packet.JID;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.Message;

import java.util.List;

public interface CallSessionMapper {

    public CallSession getSession(String id);

    public CallSession getSession(Message message) throws JingleException;

    public CallSession getSession(JingleIQ iq);

    public void addSession(CallSession callSession);

    public void removeSession(CallSession callSession);

    public String getSessionId(Message message) throws JingleException;

    public String getSessionId(JingleIQ iq);

    public CallSession addSentRequest(Message message) throws JingleException;

    public CallSession addReceivedRequest(Message message) throws JingleException;

    public CallSession addSentResponse(Message message) throws JingleException;

    public CallSession addReceivedResponse(Message message) throws JingleException;

    public CallSession addSentJingle(JingleIQ jingle);

    public CallSession addReceivedJingle(JingleIQ jingle);

    public List<CallSession> getTimeoutSessions(int ms, int max);

    public int getSessionCount();

    public int getPendingSessionCount();

    public void clearSessionFromUser(final JID user);

    public void destroy();

}
