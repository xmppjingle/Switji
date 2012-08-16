package org.jinglenodes.custom;

import org.jinglenodes.callkiller.CallKiller;
import org.jinglenodes.jingle.Reason;
import org.jinglenodes.prepare.CallPreparation;
import org.jinglenodes.session.CallSession;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipChannel;

/**
 * Created with IntelliJ IDEA.
 * User: thiago
 * Date: 8/6/12
 * Time: 11:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class RingHangupPreparation extends CallPreparation {

    private CallKiller callKiller;
    private int sleepTime;

    @Override
    public boolean prepareInitiate(JingleIQ iq, CallSession session) {
        return true;
    }

    @Override
    public boolean proceedInitiate(JingleIQ iq, CallSession session) {
        return true;
    }

    @Override
    public boolean proceedTerminate(JingleIQ iq, CallSession session) {
        return true;
    }

    @Override
    public boolean proceedAccept(JingleIQ iq, CallSession session) {
        return true;
    }

    @Override
    public void proceedInfo(JingleIQ iq, CallSession session) {
        if (session != null) {
            callKiller.immediateKill(session, new Reason(Reason.Type.cancel));
        } else {
            callKiller.immediateKill(iq, new Reason(Reason.Type.cancel));
        }
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
        if (session != null) {
            //callKiller.immediateKill(session, new Reason(Reason.Type.cancel));
            callKiller.scheduleKill(session, sleepTime);
        } else {
            //callKiller.immediateKill(iq, new Reason(Reason.Type.cancel));
            callKiller.scheduleKill(iq, sleepTime);
        }
    }

    @Override
    public JingleIQ proceedSIPEarlyMedia(JingleIQ iq, CallSession session, SipChannel channel) {
        return iq;
    }

    @Override
    public JingleIQ proceedSIPTerminate(JingleIQ iq, CallSession session, SipChannel channel) {
        return iq;
    }

    @Override
    public JingleIQ proceedSIPAccept(JingleIQ iq, CallSession session, SipChannel channel) {
        return iq;
    }

    public CallKiller getCallKiller() {
        return callKiller;
    }

    public void setCallKiller(CallKiller callKiller) {
        this.callKiller = callKiller;
    }

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }
}
