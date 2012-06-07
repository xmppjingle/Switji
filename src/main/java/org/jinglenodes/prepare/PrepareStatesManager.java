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

package org.jinglenodes.prepare;

import org.jinglenodes.jingle.Reason;
import org.jinglenodes.session.CallSession;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipChannel;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 3/23/12
 * Time: 4:35 PM
 */
public interface PrepareStatesManager {
    public void prepareCall(final JingleIQ iq, final CallSession session);

    public void proceedCall(final JingleIQ iq, final CallSession session);

    public void cancelCall(final JingleIQ iq, final CallSession session, final Reason reason);

    public void prepareCall(final Message msg, final CallSession session, final SipChannel channel);

    public void proceedCall(final Message msg, final CallSession session, final SipChannel channel);

    public void cancelCall(final Message msg, final CallSession session, final SipChannel channel, final Reason reason);
}
