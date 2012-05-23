package org.xmpp.tinder;

import org.dom4j.Element;
import org.jinglenodes.jingle.Jingle;
import org.jinglenodes.jingle.content.Content;
import org.jinglenodes.jingle.description.Description;
import org.jinglenodes.jingle.description.Payload;
import org.jinglenodes.jingle.transport.Candidate;
import org.jinglenodes.jingle.transport.RawUdpTransport;
import org.xmpp.packet.IQ;

import java.util.ArrayList;
import java.util.List;

public class JingleIQ extends IQ {

    private final Jingle jingle;

    public JingleIQ(final Jingle element) {
        super(Type.set);
        this.jingle = element;
        this.setChildElement(element);
    }

    public static JingleIQ fromXml(final IQ iq) {
        System.out.println("IQ: " + iq);
        Element je = iq.getChildElement();
        Element ce = null;
        if (je == null) {
            je = iq.getElement();
            ce = iq.getElement().element("content");
        }
        if (je != null) {
            if (!"jingle".equals(je.getName())) {
                je = je.element("jingle");
            }
            if (null == ce){
                //TODO GET CONTENT ELEMENT
                ce = null;
            }


            final String sid = je.attributeValue("sid");
            final String initiator = je.attributeValue("initiator");
            final String responder = je.attributeValue("responder");
            final String action = je.attributeValue("action");
            final Jingle jingle = new Jingle(sid, initiator, responder, action);

            final Content content = extractContent(ce);
            jingle.setContent(content);

            final JingleIQ jingleIQ = new JingleIQ(jingle);
            jingleIQ.setTo(iq.getTo());
            jingleIQ.setFrom(iq.getFrom());
            jingleIQ.setID(iq.getID());
            jingleIQ.setType(iq.getType());
            return jingleIQ;
        }
        return null;
    }

    private static Content extractContent(Element element) {
        final Content.Creator creator = Content.Creator.valueOf(element.attributeValue("creator"));
        final String name = element.attributeValue("name");
        final Content.Senders senders = Content.Senders.valueOf(element.attributeValue("senders"));
        final Element de = element.element("description");
        final Description description = extractDescription(de);
        final Element te = element.element("transport");
        final RawUdpTransport raw = extractRawUdpTransport(te);
        return new Content(creator, name, senders, description, raw);
    }

    private static Description extractDescription(Element element) {
        final List<Payload> payloadList = extractPayload(element);
        final Description description = new Description(element.attributeValue("media"));
        description.addPayload(payloadList);
        return description;
    }

    private static List<Payload> extractPayload(Element description) {
        final List<Payload> payloadList = new ArrayList<Payload>();
        final List<Element> elementList = (List<Element>) description.elements();
        String id, name, clockrate, channels;
        Payload payload;
        for (Element element : elementList) {
            id = element.attributeValue("id");
            name = element.attributeValue("name");
            clockrate = element.attributeValue("clockrate");
            channels = element.attributeValue("channels");
            payload = new Payload(id, name);
            if (null != clockrate)
                payload.setClockrate(Integer.parseInt(clockrate));
            if (null != channels)
                payload.setClockrate(Integer.parseInt(channels));
            payloadList.add(payload);
        }
        return payloadList;
    }

    private static RawUdpTransport extractRawUdpTransport(Element transport) {
        final List<Candidate> candidateList = extractCandidate(transport);
        return new RawUdpTransport(candidateList);
    }

    private static List<Candidate> extractCandidate(Element transport) {
        final List<Candidate> candidateList = new ArrayList<Candidate>();
        final List<Element> elementList = (List<Element>) transport.elements();
        for (Element element : elementList) {
            candidateList.add(new Candidate(element.attributeValue("ip"), element.attributeValue("port"), element.attributeValue("generation")));
        }
        return candidateList;
    }

    public static JingleIQ clone(final JingleIQ iq) {
        final JingleIQ jingleIQ = new JingleIQ(iq.getJingle());
        jingleIQ.setTo(iq.getTo());
        jingleIQ.setFrom(iq.getFrom());
        jingleIQ.setID(iq.getID());
        jingleIQ.setType(iq.getType());
        return jingleIQ;
    }

    public Jingle getJingle() {
        return jingle;
    }
}
