package org.jinglenodes.retry;

import org.apache.log4j.Logger;
import org.jinglenodes.session.CallSession;

import java.util.concurrent.Callable;

/**
 * Retry task
 * @author bhlangonijr
 */
public class RetryTask implements Callable<CallSession> {

    final Logger log = Logger.getLogger(RetryTask.class);
    private final long timestamp = System.currentTimeMillis();
    private final CallSession session;
    private final RetryPreparation preparation;

    public RetryTask(CallSession session, RetryPreparation preparation) {
        this.session = session;
        this.preparation = preparation;
    }

    @Override
    public CallSession call() throws Exception {

        log.debug("Executing retry task: "+session.getId());

        preparation.getPendingCalls().remove(session.getId());

        if (!session.isCallKilled()  && !session.isConnected() &&
                System.currentTimeMillis()-timestamp >= preparation.getCallTimeout() &&
                session.getAcceptIQ() == null) {
            preparation.handleRetry(session);
        } else {
            log.warn("Dismissing retry task: "+session.getId());

        }

        return session;
    }
}
