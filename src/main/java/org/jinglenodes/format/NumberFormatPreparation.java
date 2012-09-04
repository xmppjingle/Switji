package org.jinglenodes.format;

import org.apache.log4j.Logger;
import org.jinglenodes.prepare.CallPreparation;
import org.jinglenodes.session.CallSession;
import org.xmpp.packet.JID;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.JIDFactory;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipChannel;

/**
 * Created with IntelliJ IDEA.
 * User: thiago
 * Date: 8/31/12
 * Time: 2:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class NumberFormatPreparation extends CallPreparation {

    final static Logger log = Logger.getLogger(NumberFormatPreparation.class);

    @Override
    public boolean prepareInitiate(JingleIQ iq, CallSession session) {
        return true;
    }

    @Override
    public boolean proceedInitiate(JingleIQ iq, CallSession session) {
        formatNumbers(iq);
        return true;
    }

    @Override
    public boolean proceedTerminate(JingleIQ iq, CallSession session) {
        formatNumbers(iq);
        return true;
    }

    @Override
    public boolean proceedAccept(JingleIQ iq, CallSession session) {
        formatNumbers(iq);
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
        return formatNumbers(iq);
    }

    @Override
    public void proceedSIPInfo(JingleIQ iq, CallSession session, SipChannel channel) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public JingleIQ proceedSIPEarlyMedia(JingleIQ iq, CallSession session, SipChannel channel) {
        return iq;
    }

    @Override
    public JingleIQ proceedSIPTerminate(JingleIQ iq, CallSession session, SipChannel channel) {
        return formatNumbers(iq);
    }

    @Override
    public JingleIQ proceedSIPAccept(JingleIQ iq, CallSession session, SipChannel channel) {
        return formatNumbers(iq);
    }

    private JingleIQ formatNumbers(JingleIQ iq) {
        final JID rj = JIDFactory.getInstance().getJID(iq.getJingle().getResponder());
        String toNode = rj.getNode();
        if (toNode.indexOf("00") == 0) {
            toNode = toNode.substring(2);
        } else if (toNode.charAt(0) == '+') {
            toNode = toNode.substring(1);
        }
        iq.getJingle().setResponder(toNode + "@" + rj.getDomain() + "/" + rj.getResource());

        final JID ij = JIDFactory.getInstance().getJID(iq.getJingle().getInitiator());
        String fromNode = ij.getNode();
        if (fromNode.indexOf("00") == 0) {
            fromNode = fromNode.substring(2);
        } else if (fromNode.charAt(0) == '+') {
            fromNode = fromNode.substring(1);
        }
        iq.getJingle().setInitiator(fromNode + "@" + ij.getDomain() + "/" + ij.getResource());
        return iq;
    }
}
