package org.jinglenodes.charge;

import java.io.Serializable;

/**
 * Online Charge Session <br>
 *
 * Keep track of the charged seconds sent by {@link org.jinglenodes.charge.OnlineChargeServiceProcessor}
 *
 * @author bhlangonijr
 *         Date: 3/3/14
 *         Time: 10:04 PM
 */
public class OnlineChargeSession implements Serializable {

    private static final int DEFAULT_CHARGE_SECONDS = 60; // 60 seconds default

    private final int chargeSeconds;
    private final String initiator;
    private final String responder;
    private final long startTime;
    private long endTime;
    private int chargeCount; // value changed by a single thread a time, no need to handle concurrency

    public OnlineChargeSession(int chargeSeconds, String initiator, String responder, long startTime) {
        this.chargeSeconds = chargeSeconds;
        this.initiator = initiator;
        this.responder = responder;
        this.startTime = startTime;
    }

    public OnlineChargeSession(String initiator, String responder, long startTime) {
        this(DEFAULT_CHARGE_SECONDS, initiator, responder, startTime);
    }

    public int getChargeSeconds() {
        return chargeSeconds;
    }

    public String getInitiator() {
        return initiator;
    }

    public String getResponder() {
        return responder;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getChargeCount() {
        return chargeCount;
    }

    public void setChargeCount(int chargeCount) {
        this.chargeCount = chargeCount;
    }

    /**
     * This method should be called by only one thread a time
     */
    public void incChargeCount() {
        this.chargeCount++;
    }
}
