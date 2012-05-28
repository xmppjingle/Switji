package org.xmpp;

import junit.framework.TestCase;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.jinglenodes.jingle.info.Info;
import org.jinglenodes.jingle.Jingle;
import org.jinglenodes.jingle.reason.Reason;
import org.jinglenodes.jingle.content.Content;
import org.jinglenodes.jingle.description.Description;
import org.jinglenodes.jingle.description.Payload;
import org.jinglenodes.jingle.processor.JingleProcessor;
import org.jinglenodes.jingle.processor.JingleSipException;
import org.jinglenodes.jingle.reason.ReasonType;
import org.jinglenodes.jingle.transport.Candidate;
import org.jinglenodes.jingle.transport.RawUdpTransport;
import org.jinglenodes.sip.processor.SipProcessor;
import org.xmpp.packet.IQ;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.Message;

public class TestParser extends TestCase {

    final private String source = "<jingle xmlns=\"urn:xmpp:jingle:1\" sid=\"abc\" initiator=\"a@a.com\" responder=\"b@b.com\" action=\"session-initiate\">\n" +
            "  <content creator=\"initiator\" name=\"audio\" senders=\"both\">\n" +
            "    <description xmlns=\"urn:xmpp:jingle:apps:rtp:1\" media=\"audio\">\n" +
            "      <payload-type xmlns=\"\" id=\"18\" name=\"G729\" clockrate=\"8000\" channels=\"1\"/>\n" +
            "    </description>\n" +
            "    <transport xmlns=\"urn:xmpp:jingle:transports:raw-udp:1\">\n" +
            "      <candidate ip=\"10.166.108.22\" port=\"10000\" generation=\"0\" type=\"host\"/>\n" +
            "    </transport>\n" +
            "  </content>\n" +
            "</jingle>";
    final private String altSource = "<jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-initiate\" sid=\"abc\" initiator=\"a@a.com\" responder=\"b@b.com\">  <content creator=\"initiator\" name=\"audio\" senders=\"both\"><description xmlns=\"urn:xmpp:jingle:apps:rtp:1\" media=\"audio\">      <payload-type id=\"18\" name=\"G729\" clockrate=\"0\" channels=\"1\"/></description><transport xmlns=\"urn:xmpp:jingle:transports:raw-udp:1\">      <candidate ip=\"10.166.108.22\" port=\"10000\" generation=\"0\" type=\"host\"/></transport></content></jingle>";
    final String initiator = "a@a.com";
    final String responder = "b@b.com";

    public void testGenParser() {
        final Jingle jingle = new Jingle("abc", initiator, responder, Jingle.Action.session_initiate);
        jingle.setContent(new Content(Content.Creator.initiator, "audio", Content.Senders.both, new Description("audio"), new RawUdpTransport(new Candidate("10.166.108.22", "10000", "0"))));
        jingle.getContent().getDescription().addPayload(Payload.G729);
        final JingleIQ jingleIQ = new JingleIQ(jingle);
        //assertEquals(source, jingleIQ.getChildElement().asXML());
        System.out.println(jingleIQ.toString());

        final JingleIQ jingleIQParsed = JingleIQ.fromXml(jingleIQ);
        System.out.println(jingleIQParsed.getJingle().asXML());

        assertEquals(source, jingleIQParsed.getJingle().toString());
        assertEquals(initiator, jingleIQParsed.getJingle().getInitiator());
    }

    final private String sourceTerminate = "<jingle xmlns=\"urn:xmpp:jingle:1\" sid=\"abc\" initiator=\"a@a.com\" responder=\"b@b.com\" action=\"session-terminate\">\n" +
            "<reason/>\n" +
            "  <success/>\n    </reason>\n</jingle>";


    public void testDoubleParse() throws DocumentException {

        final String initiator = "romeo@localhost";
        final String responder = "juliet@localhost";
        final String packet = "<jingle xmlns=\"urn:xmpp:jingle:1\" sid=\"37665\" initiator=\"" + initiator + "\" responder=\"" + responder + "\" action=\"session-initiate\"><content xmlns=\"\" creator=\"initiator\" name=\"audio\" senders=\"both\"><description xmlns=\"urn:xmpp:jingle:apps:rtp:1\"><payload-type xmlns=\"\" id=\"0\" name=\"PCMU\" clockrate=\"8000\" channels=\"1\"/></description><transport xmlns=\"urn:xmpp:jingle:transports:raw-udp:1\"><candidate xmlns=\"\" ip=\"192.168.20.172\" port=\"22000\" generation=\"0\" type=\"host\"/></transport></content></jingle>";

        Document doc = DocumentHelper.parseText(packet);

        final IQ iq = new IQ(doc.getRootElement());
        final JingleIQ jingleIQ = JingleIQ.fromXml(iq);
        jingleIQ.setFrom(initiator);
        jingleIQ.setTo("sip.localhost");
        assertEquals(doc.getRootElement().asXML(), jingleIQ.getJingle().toString());

        final JingleIQ newJingle = JingleIQ.fromXml(jingleIQ);
        assertTrue(newJingle.getJingle().getContent().getDescription() != null);
    }

    public void testGenParserTerminate() {
        final Jingle jingle = new Jingle("abc", initiator, responder, Jingle.Action.session_terminate);
        jingle.setReason(new Reason(new ReasonType(ReasonType.Name.success)));
        final JingleIQ jingleIQ = new JingleIQ(jingle);

        System.out.println(jingleIQ.toXML());
        assertEquals(sourceTerminate, jingleIQ.getChildElement().toString());

        final JingleIQ jingleIQParsed = JingleIQ.fromXml(jingleIQ);
        System.out.println(jingleIQParsed.getChildElement().asXML());
        assertEquals(sourceTerminate, jingleIQParsed.getChildElement().asXML());
        assertEquals(initiator, jingleIQParsed.getJingle().getInitiator());
    }


    public void testGenInfo() throws DocumentException{
        final String packet = "<iq type=\"set\" id=\"hg4891f5\" to=\"romeo@montague.lit/orchard\" from=\"juliet@capulet.lit/balcony\"> <jingle xmlns=\"urn:xmpp:jingle:1\" sid=\"a73sjjvkla37jfea\" initiator=\"romeo@montague.lit/orchard\" action=\"session-info\"> <mute xmlns=\"urn:xmpp:jingle:apps:rtp:info:1\" name=\"voice\" creator=\"responder\"/> </jingle> </iq>";
        Document doc = DocumentHelper.parseText(packet);

        final IQ iq = new IQ(doc.getRootElement());
        final JingleIQ jingleIQ = JingleIQ.fromXml(iq);
        System.out.println(jingleIQ);
        Info info = jingleIQ.getJingle().getInfo();
        System.out.println(info.asXML());

        assertEquals(iq.toString(), jingleIQ.toString());
    }

    public void testRingingPacket(){
        final String initiator = "romeo@localhost";
        final String responder = "juliet@localhost";

        final Jingle jingle = new Jingle("12121", initiator, responder, Jingle.Action.session_info);
        jingle.setInfo(new Info(Info.Type.ringing));
        final JingleIQ iq = new JingleIQ(jingle);
        iq.setTo(initiator);
        iq.setFrom(responder);

        assertEquals(jingle.toString(), iq.getJingle().toString());
        assertEquals(Info.Type.valueOf("ringing"), iq.getJingle().getInfo().getType());
    }


    public void testSIPParsing() throws JingleSipException {
        final String sipString = "SIP/2.0 200 OK\n" +
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

        final Message m = new Message(sipString);

        final Content c = SipProcessor.getContent("v=0\n" +
                "o=root 2139395421 2139395422 IN IP4 194.183.72.28\n" +
                "s=Asterisk PBX 1.6.0.26-FONCORE-r78\n" +
                "c=IN IP4 194.183.72.28\n" +
                "t=0 0\n" +
                "m=audio 0 RTP/AVP 18 112 3 0");


        System.out.println(m.toString());
    }


    final String initiateExample = "<iq type=\"set\" id=\"880BF095-217C-4723-A544-8AB154E17BA0\" to=\"sip.yuilop.tv\" from=\"+4915634567890" +
            "@yuilop.tv/I(1.4.0.20120515)(Xx/IHylQbJOau1uE6xiQua39scU=)\">\n<jingle xmlns=\"urn:xmpp:jingle:1\" sid=\"65A377CF25AD46D7B5A324F063002247\" initiator=\"+4915634567890@yuilop.tv/I(1.4.0" +
            ".20120515)(Xx/IHylQbJOau1uE6xiQua39scU=)\" responder=\"004915738512829@sip.yuilop.tv\" action=\"session-initiate\">\n" +
            "  <content creator=\"initiator\" name=\"voice\">\n" +
            "    <description xmlns=\"urn:xmpp:jingle:apps:rtp:1\" media=\"audio\">\n" +
            "      <payload-type id=\"0\" name=\"PCMU\" clockrate=\"8000\" channels=\"1\"/>\n" +
            "      <payload-type id=\"8\" name=\"PCMA\" clockrate=\"8000\" channels=\"1\"/>\n" +
            "      <payload-type id=\"104\" name=\"iLBC\" clockrate=\"8000\" channels=\"1\"/>\n" +
            "      <payload-type id=\"18\" name=\"G729\" clockrate=\"8000\" channels=\"1\"/>\n" +
            "      <payload-type id=\"3\" name=\"GSM\" clockrate=\"8000\" channels=\"1\"/>\n" +
            "    </description>\n" +
            "    <transport xmlns=\"urn:xmpp:jingle:transports:raw-udp:1\">\n" +
            "      <candidate ip=\"10.166.108.174\" port=\"4000\" type=\"host\"/>\n" +
            "    </transport>\n" +
            "  </content>\n" +
            "</jingle>\n</iq> ";

    public void testInitiate() throws DocumentException{
        Document doc = DocumentHelper.parseText(initiateExample);
        final IQ iq = new IQ(doc.getRootElement());
        final JingleIQ jingleIQ = JingleIQ.fromXml(iq);
        assertEquals(iq.toString(), jingleIQ.toString());
    }

    final String initiateNoPayloadExample = "<iq type=\"set\" id=\"880BF095-217C-4723-A544-8AB154E17BA0\" to=\"sip.yuilop.tv\" from=\"+4915634567890" +
            "@yuilop.tv/I(1.4.0.20120515)(Xx/IHylQbJOau1uE6xiQua39scU=)\">\n<jingle xmlns=\"urn:xmpp:jingle:1\" sid=\"65A377CF25AD46D7B5A324F063002247\" initiator=\"+4915634567890@yuilop.tv/I(1.4.0" +
            ".20120515)(Xx/IHylQbJOau1uE6xiQua39scU=)\" responder=\"004915738512829@sip.yuilop.tv\" action=\"session-initiate\">\n" +
            "  <content creator=\"initiator\" name=\"voice\">\n" +
            "    <description xmlns=\"urn:xmpp:jingle:apps:rtp:1\" media=\"audio\">\n" +
            "    </description>\n" +
            "    <transport xmlns=\"urn:xmpp:jingle:transports:raw-udp:1\">\n" +
            "      <candidate ip=\"10.166.108.174\" port=\"4000\" type=\"host\"/>\n" +
            "    </transport>\n" +
            "  </content>\n" +
            "</jingle>\n</iq> ";

    public void testInitiateNoPayload() throws DocumentException{
        Document doc = DocumentHelper.parseText(initiateNoPayloadExample);
        final IQ iq = new IQ(doc.getRootElement());
        final JingleIQ jingleIQ = JingleIQ.fromXml(iq);
        assertEquals(iq.toString(), jingleIQ.toString());
    }

     final String acceptExample = "<iq type=\"set\" id=\"73-62\" to=\"+4915634567890@yuilop.tv/I(1.4.0.20120515)(Xx/IHylQbJOau1uE6xiQua39scU=)\" from=\"0049" +
             "15738512829@sip.yuilop.tv\">\n<jingle xmlns=\"urn:xmpp:jingle:1\" sid=\"65A377CF25AD46D7B5A324F063002247\" initiator=\"+4915634567890@194.183.72.28/sip\" responder=\"004915738512829@sip.yu" +
             "ilop.tv\" action=\"session-accept\">\n" +
             "  <content creator=\"initiator\" name=\"root\" senders=\"both\">\n" +
             "    <description xmlns=\"urn:xmpp:jingle:apps:rtp:1\" media=\"audio\">\n" +
             "      <payload-type id=\"18\" name=\"G729\" clockrate=\"8000\" channels=\"1\"/>\n" +
             "      <payload-type id=\"3\" name=\"GSM\" clockrate=\"8000\" channels=\"1\"/>\n" +
             "      <payload-type id=\"8\" name=\"PCMA\" clockrate=\"8000\" channels=\"1\"/>\n" +
             "      <payload-type id=\"0\" name=\"PCMU\" clockrate=\"8000\" channels=\"1\"/>\n" +
             "    </description>\n" +
             "    <transport xmlns=\"urn:xmpp:jingle:transports:raw-udp:1\">\n" +
             "      <candidate ip=\"87.230.83.87\" port=\"6070\" generation=\"0\" type=\"host\"/>\n" +
             "    </transport>\n" +
             "  </content>\n" +
             "</jingle>\n</iq>";

    public void testAccept() throws DocumentException{
        Document doc = DocumentHelper.parseText(acceptExample);
        final IQ iq = new IQ(doc.getRootElement());
        final JingleIQ jingleIQ = JingleIQ.fromXml(iq);
        assertEquals(iq.toString(), jingleIQ.toString());
    }

    final String terminateExample = "<iq type=\"set\" id=\"758-53\" to=\"+4915634567890@yuilop.tv/I(1.4.0.20120515)(Xx/IHylQbJOau1uE6xiQua39scU=)\" from=\"004" +
            "915738512829@sip.yuilop.tv/as5a1f65c0\">\n<jingle xmlns=\"urn:xmpp:jingle:1\" sid=\"2E9C45EB2AF84F59BDB4D281060B63AF\" initiator=\"+4915634567890@194.183.72.28/sip\" responder=\"0049157\n" +
            "38512829@sip.yuilop.tv/as5a1f65c0\" action=\"session-terminate\">\n" +
            "  <reason>\n" +
            "    <general-error/>\n" +
            "  </reason>\n" +
            "</jingle>\n</iq>";

    public void testTerminate() throws DocumentException{
        Document doc = DocumentHelper.parseText(terminateExample);
        final IQ iq = new IQ(doc.getRootElement());
        final JingleIQ jingleIQ = JingleIQ.fromXml(iq);
        assertEquals(iq.toString(), jingleIQ.toString());
    }

    final String infoExample = "<iq type=\"set\" id=\"134-61\" to=\"+4915634567890@yuilop.tv/I(1.4.0.20120515)(Xx/IHylQbJOau1uE6xiQua39scU=)\" from=\"004" +
            "915738512829@sip.yuilop.tv/as677d099c\">\n  <jingle xmlns=\"urn:xmpp:jingle:1\" sid=\"65A377CF25AD46D7B5A324F063002247\" initiator=\"+4915634567890@194.183.72.28/sip\" responder=\"004915738512" +
            "829@sip.yuilop.tv/as677d099c\" action=\"session-info\">\n" +
            "    <ringing xmlns=\"urn:xmpp:jingle:apps:rtp:info:1\"/>\n" +
            "  </jingle>\n</iq>";

    public void testInfo() throws DocumentException{
        Document doc = DocumentHelper.parseText(infoExample);
        final IQ iq = new IQ(doc.getRootElement());
        final JingleIQ jingleIQ = JingleIQ.fromXml(iq);
        assertEquals(iq.toString(), jingleIQ.toString());
    }

}
