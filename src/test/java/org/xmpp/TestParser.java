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
import org.jinglenodes.jingle.reason.ReasonType;
import org.jinglenodes.jingle.transport.Candidate;
import org.jinglenodes.jingle.transport.RawUdpTransport;
import org.xmpp.packet.IQ;
import org.xmpp.tinder.JingleIQ;

public class TestParser extends TestCase {

    final private String source = "<jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-initiate\" sid=\"abc\" initiator=\"a@a.com\" responder=\"b@b.com\">\n" +
            "  <content creator=\"initiator\" name=\"audio\" senders=\"both\">\n" +
            "    <description xmlns=\"urn:xmpp:jingle:apps:rtp:1\" media=\"audio\"></description>\n" +
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
        //assertEquals(jingleIQ.getChildElement().element("jingle").asXML(), source);
        System.out.println(jingleIQ.toXML());
        final JingleIQ jingleIQParsed = JingleIQ.fromXml(jingleIQ);
        final String jingleString = jingleIQParsed.getJingle().toString();
        System.out.println(jingleIQParsed.getJingle().asXML());
        assertEquals(source, jingleIQParsed.getJingle().asXML());
        assertEquals(jingleIQParsed.getJingle().getInitiator(), initiator);
        System.out.println(source);
    }

    final private String sourceTerminate = "<jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-terminate\" sid=\"abc\" initiator=\"a@a.com\" responder=\"b@b.com\">\n" +
            "<reason/>\n" +
            "</jingle>";


    public void testDoubleParse() throws DocumentException {

        final String initiator = "romeo@localhost";
        final String responder = "juliet@localhost";
        final String packet = "<jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-initiate\" initiator=\"" + initiator + "\" responder=\"" + responder + "\" sid=\"37665\"><content xmlns=\"\" creator=\"initiator\" name=\"audio\" senders=\"both\"><description xmlns=\"urn:xmpp:jingle:apps:rtp:1\"><payload-type xmlns=\"\" id=\"0\" name=\"PCMU\"/></description><transport xmlns=\"urn:xmpp:jingle:transports:raw-udp:1\"><candidate xmlns=\"\" ip=\"192.168.20.172\" port=\"22000\" generation=\"0\"/></transport></content></jingle>";

        Document doc = DocumentHelper.parseText(packet);

        final IQ iq = new IQ(doc.getRootElement());
        final JingleIQ jingleIQ = JingleIQ.fromXml(iq);
        jingleIQ.setFrom(initiator);
        jingleIQ.setTo("sip.localhost");

        final JingleIQ newJingle = JingleIQ.fromXml(jingleIQ);
        Content c = newJingle.getJingle().getContent();
        assertTrue(newJingle.getJingle().getContent().getDescription() != null);
    }

    public void testGenParserTerminate() {
        final Jingle jingle = new Jingle("abc", initiator, responder, Jingle.Action.session_terminate);
        jingle.setReason(new Reason(new ReasonType(ReasonType.Name.success)));
        final JingleIQ jingleIQ = new JingleIQ(jingle);
        assertEquals(jingleIQ.getChildElement().asXML(), sourceTerminate);
        System.out.println(jingleIQ.toXML());
        final JingleIQ jingleIQParsed = JingleIQ.fromXml(jingleIQ);
        System.out.println(jingleIQParsed.getChildElement().element("jingle").asXML());
        assertEquals(sourceTerminate, jingleIQParsed.getChildElement().element("jingle").asXML());
        assertEquals(jingleIQParsed.getJingle().getInitiator(), initiator);
    }

    public void testGenInfo() throws DocumentException{
        final String packet = "<iq from=\"juliet@capulet.lit/balcony\" id=\"hg4891f5\" to=\"romeo@montague.lit/orchard\" type=\"set\"> <jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-info\" initiator=\"romeo@montague.lit/orchard\" sid=\"a73sjjvkla37jfea\"> <mute xmlns=\"urn:xmpp:jingle:apps:rtp:info:1\" creator=\"responder\" name=\"voice\"/> </jingle> </iq>";
        Document doc = DocumentHelper.parseText(packet);

        final IQ iq = new IQ(doc.getRootElement());
        final JingleIQ jingleIQ = JingleIQ.fromXml(iq);
        System.out.println(jingleIQ);
        Info info = jingleIQ.getJingle().getInfo();
        System.out.println(info);
    }

    public void testRingingPacket(){

        final String initiator = "romeo@localhost";
        final String responder = "juliet@localhost";

        final Jingle jingle = new Jingle("12121", initiator, responder, Jingle.Action.session_info);
        jingle.setInfo(new Info(Info.Type.ringing));
        final JingleIQ iq = new JingleIQ(jingle);
        iq.setTo(initiator);
        iq.setFrom(responder);

        System.out.println(jingle.toString());
        System.out.println(iq.toXML());


    }
}
