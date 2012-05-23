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
        jingle.setContent(new Content(Content.Creator.initiator, "audio", Content.Senders.both, new Description("audio"), new RawUdpTransport(new Candidate("10.166.108.22", "10000", "0"))));
        jingle.getContent().getDescription().addPayload(Payload.G729);
        final JingleIQ jingleIQ = new JingleIQ(jingle);
        jingleIQ.setTo(to);
        jingleIQ.setFrom(initiator);
        log.debug(jingle.toString());
        return jingleIQ;
    }
}
