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
import org.zoolu.sip.header.StatusLine;
import org.zoolu.sip.message.Message;

import java.util.TimerTask;

/**
 * @author Thiago Rocha Camargo (thiago@jinglenodes.org) - Jingle Nodes
 *         Date: Oct 22, 2011
 *         Time: 3:59:35 PM
 */
public class SessionDestroyTask extends TimerTask {

    static final Logger log = Logger.getLogger(SessionDestroyTask.class);
    final protected CallSessionMapper callSessionMapper;
    protected CallSession callSession;
    final protected int sessionTtlInSeconds;
    final protected int unfinishedSessionTtl;

    public SessionDestroyTask(final CallSessionMapper callSessionMapper, final CallSession callSession, final int sessionTtlInSeconds, final int unfinishedSessionTtl) {
        this.callSessionMapper = callSessionMapper;
        this.callSession = callSession;
        this.sessionTtlInSeconds = sessionTtlInSeconds;
        this.unfinishedSessionTtl = unfinishedSessionTtl;
    }

    public SessionDestroyTask(final CallSessionMapper callSessionMapper, final CallSession callSession, final int sessionTtlInSeconds) {
        this(callSessionMapper, callSession, sessionTtlInSeconds, 220);
    }

    public void run() {

        final long d = System.currentTimeMillis() - callSession.getLastTimestamp();
        final float ds = d / 1000;

        if (!callSession.isActive()) {
            destroy();
        } else if (ds > sessionTtlInSeconds) {
            destroy();
        } else if (ds > unfinishedSessionTtl) {
            Message lr = callSession.getLastReceivedResponse();
            if (!is200Ok(lr)) {
                lr = callSession.getLastSentResponse();
                if (!is200Ok(lr)) {
                    destroy();
                }
            }
        }

    }

    private boolean is200Ok(final Message lr) {
        if (lr == null) {
            return false;
        }
        final StatusLine sl = lr.getStatusLine();
        if (sl == null) {
            return false;
        }
        final int c = sl.getCode();
        return c == 200;
    }

    private void destroy() {
        callSession.destroy();
        callSessionMapper.removeSession(callSession);
        callSession = null;
        this.cancel();
    }
}
