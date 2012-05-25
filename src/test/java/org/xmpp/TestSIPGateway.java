package org.xmpp;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.jinglenodes.Main;
import org.jinglenodes.jingle.Jingle;
import org.jinglenodes.jingle.Reason;
import org.jinglenodes.jingle.content.Content;
import org.jinglenodes.jingle.description.Description;
import org.jinglenodes.jingle.description.Payload;
import org.jinglenodes.jingle.processor.JingleProcessor;
import org.jinglenodes.jingle.transport.Candidate;
import org.jinglenodes.jingle.transport.RawUdpTransport;
import org.jinglenodes.session.CallSession;
import org.jinglenodes.session.persistence.PersistentCallSessionMapper;
import org.jinglenodes.sip.router.SipRoutingError;
import org.jinglenodes.sip.router.SipRoutingListener;
import org.xmpp.packet.JID;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.Message;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 3/20/12
 * Time: 3:32 PM
 */
public class TestSIPGateway extends TestCase {

    private static final Logger log = Logger.getLogger(TestSIPGateway.class);
    private final AtomicInteger sipInviteSent = new AtomicInteger(0);
    private final AtomicInteger sipAcceptSent = new AtomicInteger(0);
    private final SipServerMock sipServerMock = new SipServerMock();

    public void testStart() throws InterruptedException {

        Main.setAppDir(System.getProperty("user.dir") + "/target/test-classes/");
        Main.start("sipgatewaytest.xml");

        final JingleProcessor jingleProcessor = Main.getSipGatewayApplication().getJingleProcessor();
        final SipRoutingListener sipRoutingListener = new SipRoutingListener() {
            @Override
            public void routingSIP(Message message, JID sender) {
            }

            @Override
            public void routedSIP(Message message, JID sender) {
                System.out.println("Sent SIP Packet: " + message.toString());
                sipInviteSent.incrementAndGet();
            }

            @Override
            public void routingError(Message message, JID sender, SipRoutingError error) {
            }
        };
        Main.getSipGatewayApplication().getSipGatewayComponent().getGatewaySipRouter().addRoutingListener(sipRoutingListener);

        final JingleIQ init = fakeJingleInitiate("initiator@abc.com", "responder@abc.com", "sip.abc.com");

        jingleProcessor.processIQ(init);
        Thread.sleep(500);

        final Jingle jt = new Jingle(init.getJingle().getSid(), init.getJingle().getInitiator(), init.getJingle().getResponder(), Jingle.SESSION_TERMINATE);
        jt.setReason(new Reason(Reason.Type.no_error));
        jingleProcessor.processIQ(new JingleIQ(jt));

        for (int i = 0; i < 5; i++)
            Thread.sleep(200);

        PersistentCallSessionMapper sessionMapper = new PersistentCallSessionMapper();

        final CallSession cs = jingleProcessor.getCallSessionMapper().getSession("abc");

        String x = "";
        byte[] ba = null;
        long st = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            try {
                x = sessionMapper.toXml(cs);
                ba = sessionMapper.zip(x);
                x = sessionMapper.unzip(ba);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Elapsed " + (System.currentTimeMillis() - st) + " for 100 marshal. Compressed to: " + ba.length);

        st = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            try {
                final CallSession cs2 = sessionMapper.fromXml(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("Elapsed " + (System.currentTimeMillis() - st) + " for 100 unmarshal");

        assertEquals(1, sipInviteSent.get());

    }

    public static JingleIQ fakeJingleInitiate(final String initiator, final String responder, final String to) {
        final Jingle jingle = new Jingle("abc", initiator, responder, Jingle.SESSION_INITIATE);
        jingle.setContent(new Content("initiator", "audio", "both", new Description("audio"), new RawUdpTransport(new Candidate("10.166.108.22", "10000", "0"))));
        jingle.getContent().getDescription().addPayload(Payload.G729);
        final JingleIQ jingleIQ = new JingleIQ(jingle);
        jingleIQ.setTo(to);
        jingleIQ.setFrom(initiator);
        log.debug(jingle.toString());
        return jingleIQ;
    }

}
