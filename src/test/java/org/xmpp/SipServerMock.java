package org.xmpp;

import org.apache.log4j.Logger;
import org.jinglenodes.jingle.processor.JingleSipException;
import org.jinglenodes.sip.processor.SipProcessor;
import org.xmpp.jnodes.nio.DatagramListener;
import org.xmpp.jnodes.nio.ListenerDatagramChannel;
import org.xmpp.jnodes.nio.SelDatagramChannel;
import org.zoolu.sip.header.ContactHeader;
import org.zoolu.sip.header.StatusLine;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.Participants;
import org.zoolu.sip.message.SipParsingException;
import org.zoolu.sip.message.SipResponses;
import org.zoolu.sip.provider.SipProviderInfoInterface;
import org.zoolu.sip.provider.SipProviderInformation;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class SipServerMock implements DatagramListener {

    private static final Logger log = Logger.getLogger(SipServerMock.class);

    private int port = 5062;
    final private ConcurrentHashMap<String, SocketAddress> users = new ConcurrentHashMap<String, SocketAddress>();
    final private SipProviderInfoInterface sipProvider = new SipProviderInformation("127.0.0.1", port);
    private SelDatagramChannel channel = null;
    final private Random random = new Random();
    private int remotePort = 5060;

    public SipServerMock() {
        for (int i = 0; channel == null && i < 10; i++, port++) {
            try {
                channel = SelDatagramChannel.open(this, new InetSocketAddress("127.0.0.1", port));
                channel.setDatagramListener(this);
            } catch (IOException e) {
                channel = null;
            }
        }
    }

    public void datagramReceived(final ListenerDatagramChannel listenerDatagramChannel, final ByteBuffer byteBuffer, final SocketAddress address) {
        try {
            final int position = byteBuffer.position();
            final byte[] buf = new byte[position];
            byteBuffer.rewind();
            byteBuffer.get(buf, 0, position);

            if (buf.length > 5) {
                final Message message = new Message(buf, 0, buf.length);
                log.debug("Message Received: " + message.toString());
                message.setSendTo(address);
                processMessage(message);
            }
        } catch (Throwable t) {
            log.error("Could NOT Process packet.", t);
        }
    }

    private void processMessage(final Message message) throws JingleSipException {
        if (message.isRegister()) {
            processRegister(message);
        } else {
            try {
                final Participants p = Participants.getParticipants(message);
                final SocketAddress address = users.get(p.getResponder().toBareJID());
                if (address != null) message.setSendTo(address);
                reply(message);
            } catch (SipParsingException e) {
                log.error("Could NOT Extract Participants.", e);
            }
        }
    }

    private void processRegister(final Message message) throws JingleSipException {
        final Message ok = SipProcessor.createSipOk(message, sipProvider);

        if (random.nextBoolean()) {
            ok.setStatusLine(new StatusLine(SipResponses.ackRequiredCodes[random.nextInt(SipResponses.ackRequiredCodes.length)], "OK"));
        }

        ok.setSendTo(message.getSendTo());

        final ContactHeader ch = new ContactHeader(ok.getContactHeader().getNameAddress());
        ch.setExpires(1);

        ok.setContactHeader(ch);

        try {
            final Participants p = Participants.getParticipants(message);
            users.put(p.getInitiator().toBareJID(), message.getSendTo());
            reply(ok);
        } catch (SipParsingException e) {
            log.error("Could NOT Extract Participants.", e);
        }
    }

    private void reply(final Message message) {
        try {
            final int bytesSent = channel.send(ByteBuffer.wrap(message.toString().getBytes("UTF-8")), message.getSendTo());
            log.debug("[" + ((InetSocketAddress) message.getSendTo()).getHostName() + ":" + ((InetSocketAddress) message.getSendTo()).getPort() + "]Bytes sent: " + bytesSent);
        } catch (IOException e) {
            log.error("Could NOT Send packet.", e);
        }
    }

    public void injectPacket(final String str, final SocketAddress address) {

        try {
            final Message message = new Message(str);
            log.debug("Message Injected: " + message.toString());
            message.setSendTo(address);
            reply(message);
        } catch (Throwable t) {
            log.error("Could NOT Process packet.", t);
        }

    }

    public static String genSipInvite() {
        return "INVITE sip:user@192.168.20.100:16402 SIP/2.0\n" +
                "Via: SIP/2.0/UDP 192.168.20.198:16402;branch=z9hG4bK15325fd03549ca85\n" +
                "Max-Forwards: 70\n" +
                "To: \"1022\" <sip:user@192.168.20.100:16402>\n" +
                "From: \"1021\" <sip:user@192.168.20.198:16402>;tag=527520767\n" +
                "Call-ID: 658685de-dcec-11df-b1e3-80c2c2c54012@192-168-20-198\n" +
                "CSeq: 1 INVITE\n" +
                "Contact: <sip:user@192.168.20.198:16402>;isfocus\n" +
                "User-Agent: Viceroy 1.4.1\n" +
                "Content-Type: application/sdp\n" +
                "Content-Length: 666\n" +
                "\n" +
                "v=0\n" +
                "o=thiago 0 0 IN IP4 192.168.20.198\n" +
                "s=1021\n" +
                "c=IN IP4 192.168.20.198\n" +
                "b=AS:250\n" +
                "t=0 0\n" +
                "a=FLS;VRA:0;RVRA:1;AS:2\n" +
                "a=hwi:17412:2:2800\n" +
                "a=multipoint:1\n" +
                "a=iChatEncryption:YES\n" +
                "a=bandwidthDetection:NO\n" +
                "m=audio 16402 RTP/AVP 105 0\n" +
                "a=rtcp:16402\n" +
                "a=rtpmap:105 X-AAC_ELD/22050\n" +
                "a=rtpmap:0 PCMU/8000\n" +
                "a=rtpID:1261706994\n" +
                "a=fmtp:AAC SamplesPerBlock 480\n" +
                "m=video 16402 RTP/AVP 123 126 34\n" +
                "a=rtcp:16402\n" +
                "a=rtpmap:123 H264/90000\n" +
                "a=rtpmap:126 X-H264/90000\n" +
                "a=rtpmap:34 H263/90000\n" +
                "a=fmtp:34 imagesize 0\n" +
                "a=framerate:15\n" +
                "a=RTCP:AUDIO 16402 VIDEO 16402\n" +
                "a=fmtp:126 imagesize 0 rules 15:320:240:320:240:15\n" +
                "a=fmtp:123 imagesize 0 rules 15:320:240:320:240:15\n" +
                "a=rtpID:1085110790";
    }

    public int getPort() {
        return port;
    }
}
