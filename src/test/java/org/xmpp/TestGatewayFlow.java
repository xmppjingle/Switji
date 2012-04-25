package org.xmpp;

import junit.framework.TestCase;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.jinglenodes.component.SIPGatewayApplication;
import org.jinglenodes.jingle.Jingle;
import org.jinglenodes.jingle.content.Content;
import org.jinglenodes.jingle.description.Description;
import org.jinglenodes.jingle.description.Payload;
import org.jinglenodes.jingle.transport.Candidate;
import org.jinglenodes.jingle.transport.RawUdpTransport;
import org.xmpp.component.ComponentException;
import org.xmpp.component.MockExternalComponentManager;
import org.xmpp.tinder.JingleIQ;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class TestGatewayFlow extends TestCase {
    final static Logger log = Logger.getLogger(TestGatewayFlow.class);

    private SipServerMock sipServerMock;
    private SIPGatewayApplication sipGatewayApplication;

    public TestGatewayFlow() {
        BasicConfigurator.configure();
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

    public void testFlow() throws IOException, ComponentException, InterruptedException {
        final int localPort = 5060;
        final SocketAddress address = new InetSocketAddress("localhost", localPort);//InetSocketAddress.createUnresolved("localhost", localPort);
        sipGatewayApplication = new SIPGatewayApplication();
        sipGatewayApplication.setManager(new MockExternalComponentManager("127.0.0.1", 5269));
        sipGatewayApplication.init();

        sipServerMock = new SipServerMock();

        Thread.sleep(500);

        sipServerMock.injectPacket(SipServerMock.genSipInvite(), address);

        final String js = "<iq from=\"thiago@localhost/MacBook-Pro\" to=\"sip.locahost\" type=\"set\" id=\"as\"><jingle xmlns\n" +
                "=\"urn:xmpp:jingle:1\" action=\"session-initiate\" sid=\"abc\" initiator=\"thiago@locahost/MacBook-Pro \" responder=\"0000010031657118944@localhost\">\n" +
                "  <content creator=\"initiator\" name=\"audio\" senders=\"both\">\n" +
                "    <description xmlns=\"urn:xmpp:jingle:apps:rtp:1\" media=\"audio\">\n" +
                "      <payload-type id=\"18\" name=\"G729\" clockrate=\"8000\" channels=\"1\"/>\n" +
                "    </description>\n" +
                "    <transport xmlns=\"urn:xmpp:jingle:transports:raw-udp:1\">\n" +
                "      <candidate ip=\"10.166.108.22\" port=\"10000\" generation=\"0\" type=\"host\"/>\n" +
                "    </transport>\n" +
                "  </content>\n" +
                "</jingle>\n" +
                "</iq>";

        //sipGatewayApplication.getSipGatewayComponent().processIQ(JingleIQ.fromXml());

        //sipGatewayApplication.getSipGatewayComponent().processIQ(fakeJingleInitiate("123@localhost/abc", "456@localhost/def", "sip.localhost"));

        for (int i = 0; i < 1; i++)
            Thread.sleep(1500);
    }
}
