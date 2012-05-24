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

import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 3/26/12
 * Time: 11:40 AM
 */
public class SessionCleanerTask implements Runnable {

    final Logger log = Logger.getLogger(SessionCleanerTask.class);
    private Map<String, CallSession> sessionMap;
    private ScheduledThreadPoolExecutor timer;
    private int limit;
    private SessionCleanerFilter filter;
    public final static SessionCleanerFilter inactiveSessionFilter = new SessionCleanerFilter() {
        @Override
        public boolean isToBeCleaned(final CallSession session) {
            return !session.isActive();
        }
    };

    public final static SessionCleanerFilter sessionTtlFilter = new SessionCleanerFilter() {

        final private long sessionTtlInSeconds = 60 * 60 * 2;
        final private long unfinishedSessionTtl = 60 * 60 * 1;

        @Override
        public boolean isToBeCleaned(final CallSession session) {
            final long d = System.currentTimeMillis() - session.getLastTimestamp();
            final float ds = d / 1000;

            if (ds > sessionTtlInSeconds) {
                return true;
            } else if (ds > unfinishedSessionTtl) {
                if (!session.isConnected()) {
                    return true;
                }
            }
            return false;
        }
    };

    public SessionCleanerTask(Map<String, CallSession> sessionMap, ScheduledThreadPoolExecutor timer, int limit, SessionCleanerFilter filter) {
        this.sessionMap = sessionMap;
        this.timer = timer;
        this.limit = limit;
        this.filter = filter;
    }

    @Override
    public void run() {
        log.trace("Cleaning Task Running...");
        int i = 0;
        try {
            for (final CallSession callSession : sessionMap.values()) {
                if (filter.isToBeCleaned(callSession)) {
                    destroy(callSession);
                    if (limit > -1 && i++ > limit) {
                        break;
                    }
                }
            }

            timer.purge();
        } catch (Exception e) {
            log.error("Error while purging CallSession: ", e);
        }
        log.trace("Cleaning Task Finished. ");
    }

    private void destroy(final CallSession session) {
        sessionMap.remove(session.getId());
        session.destroy();
    }

    public static interface SessionCleanerFilter {
        public boolean isToBeCleaned(final CallSession session);
    }
}
