package org.jinglenodes.format;

import org.apache.log4j.Logger;
import org.jinglenodes.prepare.CallPreparation;
import org.jinglenodes.prepare.NodeFormat;
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
public class NodeFormatPreparation extends CallPreparation {

    final static Logger log = Logger.getLogger(NodeFormatPreparation.class);
    private NodeFormat responderNodeFormat;
    private NodeFormat initiatorNodeFormat;

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
        log.debug("Formating iq: " + iq.toXML());
        if (responderNodeFormat != null) {
            final JID rj = JIDFactory.getInstance().getJID(iq.getJingle().getResponder());
            String toNode = responderNodeFormat.formatNode(rj.getNode());
            iq.getJingle().setResponder(toNode + "@" + rj.getDomain() + "/" + rj.getResource());
        }

        if (initiatorNodeFormat != null) {
            final JID ij = JIDFactory.getInstance().getJID(iq.getJingle().getInitiator());
            String fromNode = initiatorNodeFormat.formatNode(ij.getNode());
            iq.getJingle().setInitiator(fromNode + "@" + ij.getDomain() + "/" + ij.getResource());
        }

        return iq;
    }

    public NodeFormat getResponderNodeFormat() {
        return responderNodeFormat;
    }

    public void setResponderNodeFormat(NodeFormat responderNodeFormat) {
        this.responderNodeFormat = responderNodeFormat;
    }

    public NodeFormat getInitiatorNodeFormat() {
        return initiatorNodeFormat;
    }

    public void setInitiatorNodeFormat(NodeFormat initiatorNodeFormat) {
        this.initiatorNodeFormat = initiatorNodeFormat;
    }
}
