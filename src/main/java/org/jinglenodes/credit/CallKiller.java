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
package org.jinglenodes.credit;

import org.apache.log4j.Logger;
import org.jinglenodes.jingle.processor.JingleProcessor;
import org.jinglenodes.session.CallSession;
import org.zoolu.tools.ConcurrentTimelineHashMap;
import org.zoolu.tools.NamingThreadFactory;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 5/23/12
 * Time: 11:31 AM
 */
public class CallKiller {

    private final Logger log = Logger.getLogger(CallKiller.class);
    private final ConcurrentTimelineHashMap<String, CallKillerTask> tasks = new ConcurrentTimelineHashMap<String, CallKillerTask>();
    private JingleProcessor jingleProcessor;
    protected ScheduledThreadPoolExecutor timerExecutor;

    public CallKiller() {
        timerExecutor = new ScheduledThreadPoolExecutor(5, new NamingThreadFactory("Call Killer Thread"));
    }

    public JingleProcessor getJingleProcessor() {
        return jingleProcessor;
    }

    public void setJingleProcessor(JingleProcessor jingleProcessor) {
        this.jingleProcessor = jingleProcessor;
    }

    public void scheduleKill(final CallSession session) {
        log.warn("Scheduling for Killing Call: " + session.getId() + " in " + session.getSessionCredit().getMaxDurationInSeconds());
        final CallKillerTask task = new CallKillerTask(session, jingleProcessor);
        tasks.put(session.getId(), task);
        timerExecutor.schedule(task, session.getSessionCredit().getMaxDurationInSeconds(), TimeUnit.SECONDS);
    }

    public void cancelKill(final CallSession session) {
        log.warn("Cancelling Schedule for Killing Call: " + session.getId());
        final CallKillerTask task = tasks.get(session.getId());
        if (task != null) {
            timerExecutor.remove(task);
        }
    }
}
