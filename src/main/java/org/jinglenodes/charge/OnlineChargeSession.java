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
    private RouteType routeType = RouteType.ip;
    private boolean charged = false;
    private final int chargeSeconds;
    private final String initiator;
    private final String responder;
    private final long startTime;
    private long endTime;
    private int chargeCount; // value changed by a single thread a time, no need to handle concurrency
    private String seqNumber;

    public OnlineChargeSession(RouteType routeType, int chargeSeconds, String initiator, String responder, long startTime) {
        this.routeType = routeType;
        this.chargeSeconds = chargeSeconds;
        this.initiator = initiator;
        this.responder = responder;
        this.startTime = startTime;
    }

    public OnlineChargeSession(String initiator, String responder, long startTime) {
        this(RouteType.ip, DEFAULT_CHARGE_SECONDS, initiator, responder, startTime);
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public boolean isCharged() {
        return charged;
    }

    public void setCharged(boolean charged) {
        this.charged = charged;
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

    public String getSeqNumber() {
        return seqNumber;
    }

    public void setSeqNumber(String seqNumber) {
        this.seqNumber = seqNumber;
    }

    /**
     * This method should be called by only one thread a time
     */
    public void incChargeCount() {
        this.chargeCount++;
    }

    public enum RouteType {
        ip, pstn
    }
}
