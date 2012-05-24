package org.xmpp.tinder;

import org.dom4j.Element;
import org.jinglenodes.jingle.Jingle;
import org.xmpp.packet.IQ;

public class JingleIQ extends IQ {

    public JingleIQ(final Jingle element) {
        super(Type.set);
        this.setChildElement(element);
    }

    public static JingleIQ fromXml(final IQ iq) {
        Element je = iq.getChildElement();
        if (je == null) {
            je = iq.getElement();
        }
        if (je != null) {
            if (!"jingle".equals(je.getName())) {
                je = je.element("jingle");
            }

            final Jingle jingle = Jingle.fromElement(je);

            final JingleIQ jingleIQ = new JingleIQ(jingle);
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
        Element element = this.getChildElement();
        if (element instanceof Jingle){
            return (Jingle) element;
        }else{
            return Jingle.fromElement(element);
        }
    }
}
