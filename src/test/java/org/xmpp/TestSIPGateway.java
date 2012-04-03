package org.xmpp;

import junit.framework.TestCase;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;
import org.jinglenodes.Main;
import org.jinglenodes.component.SIPGatewayApplication;
import org.jinglenodes.jingle.Jingle;
import org.jinglenodes.jingle.processor.JingleProcessor;
import org.jinglenodes.sip.router.SipRoutingError;
import org.jinglenodes.sip.router.SipRoutingListener;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.xmpp.packet.JID;
import org.zoolu.sip.message.Message;

import java.util.Properties;
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

        jingleProcessor.processIQ(TestGatewayFlow.fakeJingleInitiate("initiator@abc.com", "responder@abc.com", "sip.abc.com"));

        for (int i = 0; i < 1000; i++)
            Thread.sleep(500);

        assertEquals(1, sipInviteSent.get());

    }


}
