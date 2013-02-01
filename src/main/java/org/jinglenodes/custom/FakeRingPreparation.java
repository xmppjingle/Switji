package org.jinglenodes.custom;

import org.jinglenodes.prepare.CallPreparation;
import org.jinglenodes.session.CallSession;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipChannel;

public class FakeRingPreparation extends CallPreparation {

    private int sleepTime=5;

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

    public int getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
    }
}
