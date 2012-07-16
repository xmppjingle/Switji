package org.jinglenodes.prepare;

import org.jinglenodes.jingle.Reason;
import org.jinglenodes.session.CallSession;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipChannel;

/**
 * Created with IntelliJ IDEA.
 * User: thiago
 * Date: 7/16/12
 * Time: 2:30 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SipPrepareStatesManager {
    public void prepareCall(final Message msg, final CallSession session, final SipChannel channel);

    public void proceedCall(final Message msg, final CallSession session, final SipChannel channel);

    public void cancelCall(final Message msg, final CallSession session, final SipChannel channel, final Reason reason);
}
