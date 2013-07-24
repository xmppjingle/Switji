package org.jinglenodes.voicemail;

import org.apache.log4j.Logger;
import org.jinglenodes.session.CallSession;

import java.util.concurrent.Callable;

/**
 * @author bhlangonijr
 *         Date: 5/22/13
 *         Time: 5:13 PM
 */
public class VoicemailForwardTask implements Callable<CallSession> {

    final Logger log = Logger.getLogger(VoicemailForwardTask.class);
    private final long timestamp = System.currentTimeMillis();
    private final CallSession session;
    private final VoicemailPreparation preparation;

    public VoicemailForwardTask(CallSession session, VoicemailPreparation preparation) {
        this.session = session;
        this.preparation = preparation;
    }

    @Override
    public CallSession call() throws Exception {

        log.debug("Executing Voicemail forward task: "+session.getId());

        preparation.getPendingCalls().remove(session.getId());

        if (!session.isCallKilled() &&
                System.currentTimeMillis()-timestamp >= preparation.getCallTimeout()) {
            preparation.handleForwardCall(session);
        }

        return session;
    }
}
