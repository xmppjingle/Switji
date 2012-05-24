package org.xmpp;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.jinglenodes.Main;
import org.jinglenodes.jingle.Jingle;
import org.jinglenodes.jingle.reason.Reason;
import org.jinglenodes.jingle.processor.JingleProcessor;
import org.jinglenodes.jingle.reason.ReasonType;
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

        final JingleIQ init = TestGatewayFlow.fakeJingleInitiate("initiator@abc.com", "responder@abc.com", "sip.abc.com");

        jingleProcessor.processIQ(init);
        Thread.sleep(1000);

        final Jingle jt = new Jingle(init.getJingle().getSid(), init.getJingle().getInitiator(), init.getJingle().getResponder(), Jingle.Action.session_terminate);
        jt.setReason(new Reason(new ReasonType(ReasonType.Name.success))); //before no_error
        jingleProcessor.processIQ(new JingleIQ(jt));

        for (int i = 0; i < 5; i++)
            Thread.sleep(200);

        assertEquals(1, sipInviteSent.get());

    }


}
