package org.xmpp.tinder;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jinglenodes.jingle.Jingle;
import org.jinglenodes.jingle.Reason;
import org.xmpp.packet.IQ;
import org.xmpp.tinder.parser.XStreamIQ;

import java.io.StringReader;
import java.util.List;

public class JingleIQ extends XStreamIQ<Jingle> {

    final static Logger log = Logger.getLogger(JingleIQ.class);
    private final Jingle jingle;

    public JingleIQ(final Jingle element) {
        this.setType(Type.set);
        this.jingle = element;
        final Document originalDoc;
        try {
            originalDoc = new SAXReader().read(new StringReader(element.toString()));
            final Element e = originalDoc.getRootElement().createCopy();
            if (element.getReason() != null && element.getReason().getType() != null) {
                final Element reason = e.element("reason");
                if (reason != null) {
                    reason.addElement(element.getReason().getType().toString());
                }
            }
            this.element.add(e);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
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
            final String child = e.asXML().replace("\n", "");
            final Jingle j = (Jingle) JingleIQ.getStream().fromXML(child);

            //Force Initiator and Responder
            if (j.getInitiator() == null || j.getInitiator().length() < 3) {
                j.setInitiator(iq.getFrom().toString());
            }
            if (j.getResponder() == null || j.getResponder().length() < 3) {
                j.setResponder(iq.getTo().toString());
            }

            // Fix Terminate Reason
            final Element reason = e.element("reason");
            if (reason != null && j.getReason() != null) {
                final List<Element> types = reason.elements();
                if (types.size() > 0) {
                    try {
                        final Reason.Type t = Reason.Type.valueOf(types.get(0).getName());
                        j.getReason().setType(t
                        );
                    } catch (IllegalArgumentException iae) {
                        log.warn("Illegal Jingle Terminate Reason", iae);
                    }
                }
            }

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
