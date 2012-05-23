package org.xmpp;

import junit.framework.TestCase;
import org.jinglenodes.jingle.Jingle;
import org.jinglenodes.jingle.content.Content;
import org.jinglenodes.jingle.description.Description;
import org.jinglenodes.jingle.description.Payload;
import org.jinglenodes.jingle.transport.Candidate;
import org.jinglenodes.jingle.transport.RawUdpTransport;
import org.xmpp.tinder.JingleIQ;

public class TestSipJIDFilter extends TestCase {

    public static JingleIQ fakeJingleInitiate(final String initiator, final String responder, final String to) {
        final Jingle jingle = new Jingle("abc", initiator, responder, Jingle.SESSION_INITIATE);
        jingle.setContent(new Content(Content.Creator.initiator, "audio", Content.Senders.both, new Description("audio"), new RawUdpTransport(new Candidate("10.166.108.22", "10000", "0"))));
        jingle.getContent().getDescription().addPayload(Payload.G729);
        final JingleIQ jingleIQ = new JingleIQ(jingle);
        jingleIQ.setTo(to);
        jingleIQ.setFrom(initiator);
        return jingleIQ;
    }
}
