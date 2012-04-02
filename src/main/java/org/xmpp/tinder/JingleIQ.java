package org.xmpp.tinder;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jinglenodes.jingle.Jingle;
import org.xmpp.packet.IQ;
import org.xmpp.tinder.parser.XStreamIQ;

import java.io.StringReader;

public class JingleIQ extends XStreamIQ<Jingle> {

    private final Jingle jingle;

    public JingleIQ(final Jingle element) {
        this.setType(Type.set);
        this.jingle = element;
        final Document originalDoc;
        try {
            originalDoc = new SAXReader().read(new StringReader(element.toString()));
            this.element.add(originalDoc.getRootElement().createCopy());
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
