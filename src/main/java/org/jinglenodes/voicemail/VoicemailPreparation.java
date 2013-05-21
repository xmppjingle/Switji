package org.jinglenodes.voicemail;

import org.apache.log4j.Logger;
import org.jinglenodes.prepare.CallPreparation;
import org.jinglenodes.session.CallSession;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipChannel;
import org.zoolu.tools.ConcurrentTimelineHashMap;
import org.zoolu.tools.NamingThreadFactory;
import sun.jdbc.odbc.JdbcOdbcCallableStatement;

import java.util.concurrent.*;

/**
 * Voicemail preparation - handle voicemail forward/redirect
 *
 * @author bhlangonijr
 *         Date: 5/20/13
 *         Time: 3:20 PM
 */
public class VoicemailPreparation extends CallPreparation {
    final Logger log = Logger.getLogger(VoicemailPreparation.class);
    private static final int DEFAULT_MAX_ENTRIES = 20000;
    private static final long DEFAULT_CALL_TIMEOUT = 45 * 1000;

    private final ConcurrentTimelineHashMap<String, Future> pendingCall;
    private final long callTimeout;
    private ScheduledExecutorService service = Executors.newScheduledThreadPool(
            5, new NamingThreadFactory("voicemail-preparation-thread"));

    public VoicemailPreparation() {
        this(DEFAULT_CALL_TIMEOUT);
    }

    public VoicemailPreparation(long callTimeout) {
        this.pendingCall =new ConcurrentTimelineHashMap<String, Future>(DEFAULT_MAX_ENTRIES, callTimeout * 2);
        this.callTimeout = callTimeout;
    }

    @Override
    public boolean prepareInitiate(JingleIQ iq, CallSession session) {
        return true;
    }

    @Override
    public boolean proceedInitiate(JingleIQ iq, final CallSession session) {


        try {
            Future future = service.schedule(new Callable<CallSession>() {
                private final long timestamp = System.currentTimeMillis();
                @Override
                public CallSession call() throws Exception {

                    if (pendingCall.containsKey(session.getId()) &&
                            System.currentTimeMillis()-timestamp > getCallTimeout()) {
                        handleForwardCall(session);
                    }

                    try {
                        cancelTask(session.getId());
                    } catch (Exception e) {
                        log.error("Error canceling task",e);
                    }
                    return session;
                }
            }, getCallTimeout(), TimeUnit.MILLISECONDS);

            pendingCall.put(iq.getJingle().getSid(), future);

        } catch (Exception e) {
            log.error("Error initiate proceed",e);
        }


        return true;

    }

    @Override
    public boolean proceedTerminate(JingleIQ iq, CallSession session) {

        final String sid = iq.getJingle().getSid();
        // if calee hangs up before accepting the call, caller will be forwarded to voicemail
        if (pendingCall.containsKey(sid)) {
            handleForwardCall(session);
        }

        try {
            cancelTask(sid);
        } catch (Exception e) {
            log.error("Error canceling task",e);
        }

        return true;
    }

    @Override
    public boolean proceedAccept(JingleIQ iq, CallSession session) {
        try {
            cancelTask(iq.getJingle().getSid());
            if (log.isDebugEnabled()) {
                log.debug("Call accepted. Cancelling scheduled voicemail forward.. " + iq);
            }
        } catch (Exception e) {
            log.error("Error canceling task",e);
        }
        return true;
    }

    @Override
    public void proceedInfo(JingleIQ iq, CallSession session) {

    }

    @Override
    public boolean prepareInitiate(Message msg, CallSession session, SipChannel channel) {
        return true;
    }

    @Override
    public JingleIQ proceedSIPInitiate(JingleIQ iq, CallSession session, SipChannel channel) {
        return null;
    }

    @Override
    public void proceedSIPInfo(JingleIQ iq, CallSession session, SipChannel channel) {

    }

    @Override
    public JingleIQ proceedSIPEarlyMedia(JingleIQ iq, CallSession session, SipChannel channel) {
        return null;
    }

    @Override
    public JingleIQ proceedSIPTerminate(JingleIQ iq, CallSession session, SipChannel channel) {
        return null;
    }

    @Override
    public JingleIQ proceedSIPAccept(JingleIQ iq, CallSession session, SipChannel channel) {
        return null;
    }

    public long getCallTimeout() {
        return callTimeout;
    }

    private boolean cancelTask(final String sid) {

        boolean result = false;

        Future f = pendingCall.remove(sid);

        if (f != null && !f.isDone() && !f.isCancelled()) {
            result = f.cancel(true);
        }

        return result;
    }

    /*
     * forward the call to the voicemail service
     */
    private void handleForwardCall(CallSession callSession) {

        //TODO handle forward call

    }

}
