package org.jinglenodes.retry;

import org.apache.log4j.Logger;
import org.jinglenodes.jingle.processor.JingleException;
import org.jinglenodes.jingle.processor.JingleProcessor;
import org.jinglenodes.prepare.CallPreparation;
import org.jinglenodes.session.CallSession;
import org.xmpp.component.ExternalComponent;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipChannel;
import org.zoolu.tools.ConcurrentTimelineHashMap;
import org.zoolu.tools.NamingThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Retry preparation - handle retrying of sent sip messages
 *
 * @author bhlangonijr
 *
 */
public class RetryPreparation extends CallPreparation {
    final Logger log = Logger.getLogger(RetryPreparation.class);
    private static final int DEFAULT_MAX_ENTRIES = 20000;
    private static final long DEFAULT_TIMEOUT = 3 * 1000;
    private static final long DEFAULT_CALL_MAX_DURATION = 2 * 60 * 60 * 1000;
    private static final int DEFAULT_MAX_SIP_RETRIES = 3;

    private final ConcurrentTimelineHashMap<String, Future> pendingCalls;
    private final long callTimeout;
    private final ScheduledExecutorService service = Executors.newScheduledThreadPool(
            5, new NamingThreadFactory("retry-preparation-thread"));

    private final JingleProcessor jingleProcessor;
    private ExternalComponent component;
    private int maxSipRetries = DEFAULT_MAX_SIP_RETRIES;

    public RetryPreparation(JingleProcessor jingleProcessor) {
        this(DEFAULT_CALL_MAX_DURATION, DEFAULT_TIMEOUT, jingleProcessor);
    }

    public RetryPreparation(long maxCallDuration, long timeout, JingleProcessor jingleProcessor) {
        this.pendingCalls = new ConcurrentTimelineHashMap<String, Future>(DEFAULT_MAX_ENTRIES, maxCallDuration);
        this.callTimeout = timeout;
        this.jingleProcessor = jingleProcessor;
    }

    @Override
    public boolean prepareInitiate(JingleIQ iq, CallSession session) {
        return true;
    }

    @Override
    public boolean proceedInitiate(JingleIQ iq, final CallSession session) {
        try {
            scheduleRetryTask(session);
        } catch (Exception e) {
            log.error("Error initiate sip proceed",e);
        }
        return true;
    }

    @Override
    public boolean proceedTerminate(JingleIQ iq, CallSession session) {
        cancelTask(session);
        return true;

    }

    @Override
    public boolean proceedAccept(JingleIQ iq, CallSession session) {
        cancelTask(session);
        return true;
    }

    @Override
    public void proceedInfo(JingleIQ iq, CallSession session) {
        cancelTask(session);
    }

    @Override
    public boolean prepareInitiate(Message msg, CallSession session, SipChannel channel) {
        return true;
    }

    @Override
    public JingleIQ proceedSIPInitiate(JingleIQ iq, CallSession session, SipChannel channel) {
        return iq;
    }

    @Override
    public void proceedSIPInfo(JingleIQ iq, CallSession session, SipChannel channel) {
        cancelTask(session);
    }

    @Override
    public JingleIQ proceedSIPEarlyMedia(JingleIQ iq, CallSession session, SipChannel channel) {
        cancelTask(session);
        return iq;
    }

    @Override
    public JingleIQ proceedSIPTerminate(JingleIQ iq, CallSession session, SipChannel channel) {
        cancelTask(session);
        return iq;
    }

    @Override
    public JingleIQ proceedSIPAccept(JingleIQ iq, CallSession session, SipChannel channel) {
        cancelTask(session);
        return iq;
    }

    public void scheduleRetryTask(CallSession session) {

        if (session != null && session.getRetries() < getMaxSipRetries() &&
                !pendingCalls.containsKey(session.getId())) {
            if (log.isDebugEnabled()) {
                log.debug("Scheduling retry task [ Delay: " + getCallTimeout() + "]: " + session.getInitiateIQ());
            }
            final Future future = service.schedule(new RetryTask(session,this),
                    getCallTimeout(), TimeUnit.MILLISECONDS);
            pendingCalls.put(session.getId(), future);

        } else {
            log.info("Retry not scheduled for sid: "+session.getId());
        }

    }

    public long getCallTimeout() {
        return callTimeout;
    }

    public void cancelTask(CallSession session) {
        try {
            if (session != null) {
                cancelTask(session.getId());
                if (log.isDebugEnabled()) {
                    log.debug("Cancelling scheduled retry task.. " + session.getId());
                }
            }
        } catch (Exception e) {
            log.error("Error canceling task",e);
        }
    }


    public boolean cancelTask(final String sid) {
        log.debug("Cancelling task: "+sid);
        boolean result = false;
        Future f = pendingCalls.remove(sid);
        if (f != null && !f.isDone() && !f.isCancelled()) {
            result = f.cancel(true);
        }
        return result;
    }

    /*
     * Handle call retry
     */
    public void handleRetry(CallSession callSession) {

        log.info("Handling retry call: " + callSession.getId());
        final JingleIQ initiateIQ = callSession.getInitiateIQ();
        if (initiateIQ != null && getJingleProcessor() != null && !callSession.isJingleInitiator() &&
                !callSession.isConnected() && !callSession.isActive() && !callSession.isCallKilled()) {
            //retrying
            callSession.setRetries(callSession.getRetries()+1);
            try {
                getJingleProcessor().processJingle(initiateIQ);
            } catch (JingleException e) {
                log.error("Error retrying invite message", e);
            }
            log.info("sending invite retry: " + callSession.getId());
        }

    }

    public ConcurrentTimelineHashMap<String, Future> getPendingCalls() {
        return pendingCalls;
    }

    public JingleProcessor getJingleProcessor() {
        return jingleProcessor;
    }

    public ExternalComponent getComponent() {
        return component;
    }

    public void setComponent(ExternalComponent component) {
        this.component = component;
    }

    public int getMaxSipRetries() {
        return maxSipRetries;
    }

    public void setMaxSipRetries(int maxSipRetries) {
        this.maxSipRetries = maxSipRetries;
    }
}
