package org.jinglenodes.sip.router;

import org.zoolu.tools.ConcurrentTimelineHashMap;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 4/3/12
 * Time: 6:00 PM
 */
public class ThrottleManager {

    private ConcurrentTimelineHashMap<String, ThrottleMonitor> monitors = new ConcurrentTimelineHashMap<String, ThrottleMonitor>();
    private int maxPerPeriod = 60;
    private int periodInterval = 60000;
    private int expires;
    private int maxMonitors;

    public ThrottleManager(int maxPerPeriod, int periodInterval, int maxMonitors, int expires) {
        this.maxPerPeriod = maxPerPeriod;
        this.periodInterval = periodInterval;
        this.maxMonitors = maxMonitors;
        this.expires = expires;
    }

    public boolean accept(final String key) {
        ThrottleMonitor monitor = monitors.get(key);
        if (monitor == null) {
            if (monitors.size() > maxMonitors) {
                monitors.cleanUpExpired(expires);
            }
            monitor = new ThrottleMonitor(maxPerPeriod, periodInterval);
            monitors.put(key, monitor);
        }

        return monitor.accept();
    }

}
