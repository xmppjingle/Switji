package org.minijingle.xmpp.smack;

import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.PacketExtension;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.minijingle.xmpp.smack.parser.NParser;
import org.minijingle.jingle.Jingle;
import org.minijingle.jingle.transport.RawUdpTransport;
import org.xmlpull.v1.XmlPullParser;

class JingleProvider implements IQProvider {

    /**
     * Parse a iq/jingle element.
     */
    public IQ parseIQ(final XmlPullParser parser) throws Exception {

        NParser p = new NParser(parser);
        return JingleIQ.fromXml(p.getAsString());

    }

    public static void enableJingle(final XMPPConnection connection) {

        ProviderManager.getInstance().addIQProvider("jingle", Jingle.XMLNS, new JingleProvider());
        JingleIQ.getStream().alias("jingle", Jingle.class);
        JingleIQ.getStream().alias("transport", RawUdpTransport.class);
        
        Presence presence = new Presence(Presence.Type.available);
        presence.addExtension(new PacketExtension() {
            public String getElementName() {
                return "c";
            }

            public String getNamespace() {
                return "http://jabber.org/protocol/caps";
            }

            public String toXML() {
                return "<c xmlns=\"http://jabber.org/protocol/caps\" node=\"client:caps\" ver=\"0.1\" ext=\"jingle-v1\"></c>";
            }
        });

        for (int i = 0; i < 5; i++) {
            connection.sendPacket(presence);
        }
    }

}
