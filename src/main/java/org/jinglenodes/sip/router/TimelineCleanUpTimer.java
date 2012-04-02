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

package org.jinglenodes.sip.router;

import org.apache.log4j.Logger;
import org.zoolu.tools.ConcurrentTimelineHashMap;
import org.zoolu.tools.NamingThreadFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

class TimelineCleanUpTimer implements Runnable {

    private static final Logger log = Logger.getLogger(TimelineCleanUpTimer.class);
    private final long timeout;

    @SuppressWarnings("rawtypes")
    private final ConcurrentTimelineHashMap timelineHashMap;
    private final ScheduledThreadPoolExecutor cleanUpTimer = new ScheduledThreadPoolExecutor(2, new NamingThreadFactory("CleanUpTimerThread"));

    public TimelineCleanUpTimer(@SuppressWarnings("rawtypes") final ConcurrentTimelineHashMap timelineHashMap, final long timeout) {
        this.timelineHashMap = timelineHashMap;
        this.timeout = timeout;
        cleanUpTimer.scheduleWithFixedDelay(this, timeout, timeout / 2, TimeUnit.SECONDS);
    }

    public void run() {
        try {
            timelineHashMap.cleanUpExpired(timeout);
        } catch (Throwable e) {
            log.error("Severe ERROR when cleaning up old entries", e);
        }
    }

    public void shutdown() {
        cleanUpTimer.shutdownNow();
    }
}
