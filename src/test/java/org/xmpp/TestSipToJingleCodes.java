package org.xmpp;

import junit.framework.TestCase;
import org.jinglenodes.jingle.Reason;
import org.jinglenodes.sip.SipToJingleCodes;
import org.zoolu.sip.message.Message;

/**
 * @author bhlangonijr
 *         Date: 12/9/13
 *         Time: 11:29 AM
 */
public class TestSipToJingleCodes extends TestCase {


    public void testMapping() {
        String sipString = "SIP/2.0 200 OK\n" +
                "Via: SIP/2.0/UDP 194.183.72.28:5060;branch=z9hG4bK7942901908306987;received=178.33.112.237;rport=5062\n" +
                "From: \"+31651827042@yuilop.tv\" <sip:+31651827042@yuilop.tv>;tag=A(1.6)(6295399E195B795057FD01CF6D65301CB2E41499)\n" +
                "To: \"0031611537782\" <sip:0031611537782@sip.yuilop.tv>;tag=as3efe9f43\n" +
                "Call-ID: 7942901908306987\n" +
                "CSeq: 1 INVITE\n" +
                "User-Agent: Asterisk PBX 1.6.0.26-FONCORE-r78\n" +
                "Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, SUBSCRIBE, NOTIFY, INFO\n" +
                "Supported: replaces, timer\n" +
                "Contact: <sip:0031611537782@194.183.72.28>\n" +
                "Content-Type: application/sdp\n" +
                "Content-Length: 124\n" +
                "\n" +
                "v=0\n" +
                "o=root 2139395421 2139395422 IN IP4 194.183.72.28\n" +
                "s=Asterisk PBX 1.6.0.26-FONCORE-r78\n" +
                "c=IN IP4 194.183.72.28\n" +
                "t=0 0\n" +
                "m=audio 0 RTP/AVP 18 112 3 0";

        final Message m200Ok = new Message(sipString);

        assertEquals(SipToJingleCodes.getReason(m200Ok, 200).getType(), Reason.Type.success);

        sipString = "SIP/2.0 503 Service Unavailable\n" +
                "Via: SIP/2.0/UDP 213.232.148.92:0;branch=z9hG4bKA7u00491637738577x131209;received=178.33.112.237;rport=5062\n" +
                "From: \"+4915750590974@ym.ms\" <sip:+4915750590974@ym.ms>;tag=Ax1.9.3.3200xxB9D1B73D76BD2C79AEE0AA5AA8A707D046B264B4x\n" +
                "To: \"004923199219475\" <sip:004923199219475@213.232.148.92;user=phone>;tag=as3852f0e5\n" +
                "Call-ID: A7u00491637738577x131209\n" +
                "CSeq: 1 INVITE\n" +
                "Server: Asterisk PBX 1.8.13.0\n" +
                "Allow: INVITE, ACK, CANCEL, OPTIONS, BYE, REFER, SUBSCRIBE, NOTIFY, INFO, PUBLISH\n" +
                "Supported: replaces, timer\n" +
                "X-Asterisk-HangupCause: Circuit/channel congestion\n" +
                "X-Asterisk-HangupCauseCode: 34\n" +
                "Content-Length: 0";

        final Message m503 = new Message(sipString);

        assertEquals(SipToJingleCodes.getReason(m503, 503).getType(), Reason.Type.connectivity_error);

        assertEquals(SipToJingleCodes.getReason(m200Ok, 702).getType(), Reason.Type.success);

    }

}
