package org.xmpp.tinder;

import org.dom4j.Element;
import org.jinglenodes.jingle.Jingle;
import org.xmpp.packet.IQ;

public class JingleIQ extends IQ {

    private final Jingle jingle;

    public JingleIQ(final Jingle element) {
        this.setType(Type.set);
        this.jingle = element;
        this.setChildElement(element.clone());
    }

    public static JingleIQ fromXml(final IQ iq) {
        Element e = iq.getChildElement();
        if (e == null) {
            e = iq.getElement();
        }
        if (e != null) {
            if (!"jingle".equals(e.getName())) {
                e = e.element("jingle");
            }
            final String sid = e.attributeValue("sid");
            final String initiator = e.attributeValue("initiator");
            final String responder = e.attributeValue("responder");
            final String action = e.attributeValue("action");
            final Jingle j = new Jingle(sid, initiator, responder, action);
            final JingleIQ jingleIQ = new JingleIQ(j);
            jingleIQ.setTo(iq.getTo());
            jingleIQ.setFrom(iq.getFrom());
            jingleIQ.setID(iq.getID());
            jingleIQ.setType(iq.getType());
            return jingleIQ;
        }
        return null;
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
