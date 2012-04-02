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

package org.jinglenodes.sip;

import org.apache.log4j.Logger;
import org.jinglenodes.session.CallSession;
import org.jinglenodes.session.CallSessionMapper;
import org.zoolu.sip.header.CSeqHeader;
import org.zoolu.sip.header.StatusLine;
import org.zoolu.sip.message.SipResponses;
import org.zoolu.tools.NamingThreadFactory;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RetryTask extends TimerTask {

    private static final Logger log = Logger.getLogger(RetryTask.class);
    private final CallSessionMapper callSessionProvider;
    private final SipRouter defaultSipRouter;
    private final int timeout;
    private final int maxRetries;
    private final ScheduledThreadPoolExecutor executor;

    public RetryTask(final CallSessionMapper callSessionProvider, final SipRouter defaultSipRouter, final int timeout, final int maxRetries) {
        this.callSessionProvider = callSessionProvider;
        this.defaultSipRouter = defaultSipRouter;
        this.timeout = timeout;
        this.maxRetries = maxRetries;
        this.executor = new ScheduledThreadPoolExecutor(10, new NamingThreadFactory("SessionRetryThread"));
        this.executor.scheduleWithFixedDelay(this, timeout / 2, timeout / 2, TimeUnit.MILLISECONDS);
    }

    public void run() {
        log.trace("Retrying Sessions...");
        final long currentTime = System.currentTimeMillis();
        final List<CallSession> callSessions = callSessionProvider.getTimeoutSessions(timeout, 500);
        for (final CallSession callSession : callSessions) {
            try {
                if (callSession.getRetries() > maxRetries) {
                    continue;
                }
                if (callSession.getLastSentRequest() != null) {
                    if (callSession.getLastSentRequest().isAck()) {
                        continue;
                    }
                    if (callSession.getLastReceivedResponse() != null) {
                        final StatusLine sl = callSession.getLastReceivedResponse().getStatusLine();
                        if (sl != null) {
                            final int code = sl.getCode();
                            if ((code >= 200 && code < 300) || (code >= 180 && code < 190) || code == 100) {
                                final CSeqHeader rcsh = callSession.getLastReceivedResponse().getCSeqHeader();
                                final CSeqHeader scsh = callSession.getLastSentRequest().getCSeqHeader();
                                if (scsh.getMethod().equals(rcsh.getMethod()) && scsh.getSequenceNumber() == rcsh.getSequenceNumber()) {
                                    continue;
                                }
                            }

                            boolean matches = false;

                            for (final int c : SipResponses.ackRequiredCodes) {
                                if (code == c) {
                                    matches = true;
                                    break;
                                }
                            }

                            if (matches) {
                                continue;
                            }
                            continue;
                        }
                    }
                    callSession.setRetries(callSession.getRetries() + 1);
                    defaultSipRouter.routeSIP(callSession.getLastSentRequest(), callSession.getUser());
                }
            } catch (Throwable e) {
                log.error("Severe Error when Retrying", e);
            }
        }
        log.trace("Finished Retrying. Elapsed Time: " + (System.currentTimeMillis() - currentTime));
    }

    public void shutdown() {
        this.executor.shutdownNow();
    }
}
