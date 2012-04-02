package org.xmpp;

import junit.framework.TestCase;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.IQ;

public class TestConnect extends TestCase {
    protected XMPPConnection connection;
    private String usernameA="abc", usernameB="abcd";

    public void nonTestPush() {

        try {
            String server = "xmpp.com";
            String domain = "xmpp.com";
            ConnectionConfiguration conf = new ConnectionConfiguration(server, 5222, domain);
            conf.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
            conf.setSASLAuthenticationEnabled(true);
            connection = new XMPPConnection(conf);

            connection.connect();
            connection.login(usernameA, usernameA);
            connection.getRoster().setSubscriptionMode(Roster.SubscriptionMode.accept_all);

            final IQ jingleIQ = new IQ() {
                @Override
                public String getChildElementXML() {
                    return "<jingle xmlns=\"urn:xmpp:tmp:jingle\" action=\"session-initiate\" sid=\"abc\" initiator=\"a@a.com\" responder=\"b@b.com\">\n" +
                            "  <content creator=\"initiator\" name=\"audio\" senders=\"both\">\n" +
                            "    <description xmlns=\"urn:xmpp:jingle:apps:rtp:1\" media=\"audio\">\n" +
                            "      <payload-type id=\"18\" name=\"G729\" clockrate=\"0\" channels=\"1\"/>\n" +
                            "    </description>\n" +
                            "    <transport xmlns=\"urn:xmpp:jingle:transports:raw-udp:1\">\n" +
                            "      <candidate ip=\"10.166.108.22\" port=\"10000\" generation=\"0\" type=\"host\"/>\n" +
                            "    </transport>\n" +
                            "  </content>\n" +
                            "</jingle>";
                }
            };

            jingleIQ.setFrom(usernameA+"@" +domain);
            jingleIQ.setTo(usernameB + "@" + domain);
            jingleIQ.setType(IQ.Type.SET);

            connection.sendPacket(jingleIQ);


        } catch (XMPPException e) {
            e.printStackTrace();
        }

    }


}
