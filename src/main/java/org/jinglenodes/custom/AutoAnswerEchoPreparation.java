package org.jinglenodes.custom;

import org.apache.log4j.Logger;
import org.jinglenodes.jingle.Info;
import org.jinglenodes.jingle.content.Content;
import org.jinglenodes.jingle.description.Description;
import org.jinglenodes.jingle.description.Payload;
import org.jinglenodes.jingle.processor.JingleProcessor;
import org.jinglenodes.jingle.processor.JingleSipException;
import org.jinglenodes.jingle.transport.Candidate;
import org.jinglenodes.jingle.transport.RawUdpTransport;
import org.jinglenodes.prepare.CallPreparation;
import org.jinglenodes.session.CallSession;
import org.xmpp.jnodes.nio.DatagramListener;
import org.xmpp.jnodes.nio.ListenerDatagramChannel;
import org.xmpp.jnodes.nio.LocalIPResolver;
import org.xmpp.jnodes.nio.SelDatagramChannel;
import org.xmpp.packet.JID;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipChannel;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

public class AutoAnswerEchoPreparation extends CallPreparation {
    private static final Logger log = Logger.getLogger(AutoAnswerEchoPreparation.class);

    private int sleepTime = 5;
    private JingleProcessor jingleProcessor;
    private SelDatagramChannel channel;

    public AutoAnswerEchoPreparation() {
        try {
            channel = SelDatagramChannel.open(new DatagramListener() {
                public void datagramReceived(ListenerDatagramChannel channel, ByteBuffer buffer, SocketAddress address) {
                    try {
                        buffer.flip();
                        channel.send(buffer, address);
                    } catch (IOException e) {
                        log.warn("Could NOT Echo Packet", e);
                    }
                }
            }, new InetSocketAddress(System.getProperty("os.name").toLowerCase().indexOf("win") > -1 ? LocalIPResolver.getLocalIP() : "0.0.0.0", 10000));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

        try {
            log.debug("Auto Answer on IQ:" + iq.toXML());
            final JingleIQ ring = JingleProcessor.createJingleSessionInfo(new JID(iq.getJingle().getResponder()),
                    new JID(iq.getJingle().getInitiator()), iq.getFrom() != null ? iq.getFrom().toString() : null,
                    iq.getJingle().getSid(), Info.Type.ringing);
            ring.setFrom(iq.getTo());
            jingleProcessor.processIQ(ring);

            final Description description = new Description("audio");
            description.addPayload(Payload.PCMU);
            final JingleIQ answer = JingleProcessor.createJingleAccept(new JID(iq.getJingle().getResponder()),
                    new JID(iq.getJingle().getInitiator()), iq.getFrom() != null ? iq.getFrom().toString() : null,
                    new Content("responder", "audio", "both", description, new RawUdpTransport(new Candidate("localhost", "10000", "0"))), iq.getJingle().getSid());
            answer.setFrom(iq.getTo());
            jingleProcessor.processIQ(answer);

        } catch (JingleSipException e) {
            log.warn("Failed to create Ringing", e);
        }

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

    public JingleProcessor getJingleProcessor() {
        return jingleProcessor;
    }

    public void setJingleProcessor(JingleProcessor jingleProcessor) {
        this.jingleProcessor = jingleProcessor;
    }
}
