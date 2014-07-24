package org.xmpp;

import junit.framework.TestCase;
import org.jinglenodes.jingle.Jingle;
import org.jinglenodes.jingle.content.Content;
import org.jinglenodes.jingle.description.Description;
import org.jinglenodes.jingle.description.Payload;
import org.jinglenodes.jingle.processor.JingleSipException;
import org.jinglenodes.jingle.transport.Candidate;
import org.jinglenodes.jingle.transport.RawUdpTransport;
import org.jinglenodes.sip.processor.SipProcessor;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.provider.SipProviderInformation;

import javax.sdp.SdpException;
import java.net.InetSocketAddress;

/**
 * @author bhlangonijr
 *         Date: 7/22/14
 *         Time: 5:04 PM
 */
public class TestMessageRetry extends TestCase {

    private SipProviderInformation sipProviderInfo = new SipProviderInformation("127.0.0.1", 5060);

    private String msg503 = "SIP/2.0 503 Service Unavailable\n" +
            "Via: SIP/2.0/UDP 178.33.112.216:5064;branch=z9hG4bKI86udoglovex140723124841438;rport=5064\n" +
            "From: \"12066296164@ym.ms\" <sip:12066296164@ym.ms>;tag=Ix2.1.2xxf978e612041c77c53378813a50265a06a0d643edx\n" +
            "To: \"15713155551\" <sip:15713155551@68.68.124.82;user=phone>;tag=gK00b2aae3\n" +
            "Call-ID: I86udoglovex140723124841438\n" +
            "CSeq: 1 INVITE\n" +
            "Reason: Q.850;cause=41\n" +
            "Content-Length: 0";


    public void testMessage() throws JingleSipException, SdpException {


        sipProviderInfo.setViaPort(5060);

        final JingleIQ init = fakeJingleInitiate("initiator@abc.com",
                "responder@abc.com", "sip.abc.com", "abc123@123.123.123.123");
        final Message invite = SipProcessor.createSipInvite(init.getJingle(), sipProviderInfo);

        invite.setSendTo(new InetSocketAddress("127.1.1.1", 5061));

        System.out.println(invite);


    }

    public void test503Message() {

        Message msg = new Message(msg503);

        System.out.println(msg.getStatusLine().getCode());


    }




    public static JingleIQ fakeJingleInitiate(final String initiator, final String responder, final String to, final String sid) {
        final Jingle jingle = new Jingle(sid, initiator, responder, Jingle.SESSION_INITIATE);
        jingle.setContent(new Content("initiator", "audio", "both", new Description("audio"), new RawUdpTransport(new Candidate("10.166.108.22", "10000", "0"))));
        jingle.getContent().getDescription().addPayload(Payload.G729);
        jingle.getContent().getDescription().addPayload(Payload.TELEPHONE_EVENT);
        final JingleIQ jingleIQ = new JingleIQ(jingle);
        jingleIQ.setTo(to);
        jingleIQ.setFrom(initiator);
        return jingleIQ;
    }
}
