package org.jinglenodes.log;

import org.jinglenodes.prepare.CallPreparation;
import org.jinglenodes.session.CallSession;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipChannel;

/**
 * Created with IntelliJ IDEA.
 * User: thiago
 * Date: 9/28/12
 * Time: 5:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class NewRelicPreparation extends CallPreparation{
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
        //To change body of implemented methods use File | Settings | File Templates.
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
        //To change body of implemented methods use File | Settings | File Templates.
        //NewRelicRequest nreq = new NewRelicRequest(null, null, iqRequest.getRequest().getType().toString() + "_" + iqRequest.getRequest().getChildElement().getName(), this.getNamespace());

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

//    private void _dispatch(RequestImpl nreq, NewRelicResponse nres) {
//        if (nreq == null)
//            nreq = new NewRelicRequest(null, null, "Unknown", this.getNamespace());
//
//        if (nres == null)
//            nres = new NewRelicResponse(400, "Error");
//
//        NewRelic.setRequestAndResponse(nreq, nres);
//    }
}
