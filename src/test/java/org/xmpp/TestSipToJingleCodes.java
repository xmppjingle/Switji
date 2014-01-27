package org.xmpp;

import junit.framework.TestCase;
import org.jinglenodes.jingle.Reason;
import org.jinglenodes.jingle.processor.JingleException;
import org.jinglenodes.sip.SipToJingleCodes;
import org.jinglenodes.sip.processor.SipProcessor;
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



    public void testRefreshRequest() throws JingleException {
        String sipString = "INVITE sip:17607958620@178.33.112.216:5064;transport=udp SIP/2.0\n" +
                "Via: SIP/2.0/UDP 68.68.124.82:5060;branch=z9hG4bK04Bdd1645d69bdf2c0a\n" +
                "From: \"12035293763\" <sip:12035293763@68.68.124.82>;tag=gK04d9b981\n" +
                "To: \"17607958620@ym.ms\" <sip:17607958620@ym.ms>;tag=Ix2.0.20131205axx079db0c0e000d5b173df71cd3d64c3b2eec14dcdx\n" +
                "Call-ID: I60u0034634515919x131213\n" +
                "CSeq: 656516155 INVITE\n" +
                "Max-Forwards: 70\n" +
                "Allow: INVITE,ACK,CANCEL,BYE,PRACK,UPDATE,OPTIONS,MESSAGE\n" +
                "Accept: application/sdp, application/isup, application/dtmf, application/dtmf-relay,  multipart/mixed\n" +
                "Contact: <sip:12035293763@68.68.124.82:5060>\n" +
                "Supported: timer\n" +
                "Session-Expires: 1800;refresher=uac\n" +
                "Min-SE: 90\n" +
                "Content-Length: 266\n" +
                "Content-Disposition: session; handling=required\n" +
                "Content-Type: application/sdp\n" +
                "\n" +
                "v=0\n" +
                "o=Sonus_UAC 1104106171 1735972808 IN IP4 68.68.124.82\n" +
                "s=SIP Media Capabilities\n" +
                "c=IN IP4 68.68.124.84\n" +
                "t=0 0\n" +
                "m=audio 31744 RTP/AVP 18 101\n" +
                "a=rtpmap:18 G729/8000\n" +
                "a=fmtp:18 annexb=no\n" +
                "a=rtpmap:101 telephone-event/8000\n" +
                "a=fmtp:101 0-15\n" +
                "a=sendrecv\n" +
                "a=ptime:20";

        final Message msg = new Message(sipString);
        assertTrue(SipProcessor.isSessionRefresh(msg));

    }


    public void testRefreshRequest2() throws JingleException {
        String sipString = "INVITE sip:13476005162@68.68.124.82 SIP/2.0\n" +
                "Via: SIP/2.0/UDP 178.33.112.216:5064;rport;branch=z9hG4bKA9ureeceex140126\n" +
                "Max-Forwards: 70\n" +
                "To: \"13476005162\" <sip:13476005162@68.68.124.82>\n" +
                "From: \"17185049774@ym.ms\" <sip:17185049774@ym.ms>;tag=Ax2.0.3220xxFED62FC8DF37FEED83AAED1667A45A172990AE83x\n" +
                "Call-ID: A9ureeceex140126\n" +
                "CSeq: 1 INVITE\n" +
                "Contact: <sip:17185049774@178.33.112.216:5064;transport=udp>\n" +
                "Expires: 3600\n" +
                "User-Agent: Jingle Nodes Single\n" +
                "Content-Type: application/sdp\n" +
                "Content-Length: 303\n" +
                "\n" +
                "v=0\n" +
                "o=J2S 3 1 IN IP4 50.22.171.226\n" +
                "s=-\n" +
                "c=IN IP4 50.22.171.226\n" +
                "t=0 0\n" +
                "m=audio 49292 RTP/AVP 112 18 3 0 8 101\n" +
                "a=rtpmap:112 iLBC/8000/1\n" +
                "a=rtpmap:18 G729/8000/1\n" +
                "a=fmtp:18 annexb=no\n" +
                "a=rtpmap:3 GSM/8000/1\n" +
                "a=rtpmap:0 PCMU/8000/1\n" +
                "a=rtpmap:8 PCMA/8000/1\n" +
                "a=rtpmap:101 telephone-event/8000/1\n" +
                "a=sendrecv";

        final Message msg = new Message(sipString);

        assertFalse(SipProcessor.isSessionRefresh(msg));

    }

}
