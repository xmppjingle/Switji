package org.xmpp;

import junit.framework.TestCase;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.jinglenodes.relay.RelayServiceLocator;
import org.xmpp.packet.IQ;
import org.xmpp.tinder.JingleIQ;

import java.io.IOException;
import java.util.HashMap;

/**
 * @author bhlangonijr
 *         Date: 3/3/14
 *         Time: 5:34 PM
 */
public class TestRelayServiceLocator extends TestCase {


    static HashMap<String, String> relayService;
    static HashMap<String, String> relayServiceByCode;

    static  {
        relayService = new HashMap<String, String>();
        relayService.put("Ireland", "ireland.relay.ym.ms");

        relayServiceByCode = new HashMap<String, String>();
        relayServiceByCode.put("+55", "brazil.relay.ym.ms");
    }

    public void testRelayServiceLocator() throws IOException, DocumentException {

        RelayServiceLocator service = new RelayServiceLocator(
                getClass().getResource("/GeoLite2-Country.mmdb").getPath());
        service.setRelayServices(relayService);

        JingleIQ fakeIQ = getFakeIQ("54.229.106.32");

        System.out.println("FakeIQ: "+fakeIQ.toXML());

        String uri = service.getServiceUri(fakeIQ);

        System.out.println("Service = " + uri);

        assertEquals(uri, "ireland.relay.ym.ms");

        fakeIQ = getFakeIQ("189.15.25.34");

        System.out.println("FakeIQ: "+fakeIQ.toXML());

        uri = service.getServiceUri(fakeIQ);

        System.out.println("Service = " + uri);

        assertNull(uri);

    }

    public void testRelayServiceLocatorByCountryCode() throws IOException, DocumentException {

        RelayServiceLocator service = new RelayServiceLocator(
                getClass().getResource("/GeoLite2-Country.mmdb").getPath());
        service.setRelayServicesByCode(relayServiceByCode);

        JingleIQ fakeIQ = getFakeIQ("0.0.0.0");

        System.out.println("FakeIQ: "+fakeIQ.toXML());

        String uri = service.getServiceUri(fakeIQ);

        System.out.println("Service = " + uri);

        assertEquals("brazil.relay.ym.ms", uri);

    }


    private JingleIQ getFakeIQ(String ip) throws DocumentException {
        final String initiator = "+553496788900@localhost.com";
        final String responder = "+553496788901@localhost.com";
        final String packet = "<jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-initiate\" initiator=\"" + initiator + "\" responder=\"" + responder + "\" sid=\"37665\"><content xmlns=\"\" creator=\"initiator\" name=\"audio\" senders=\"both\"><description xmlns=\"urn:xmpp:jingle:apps:rtp:1\"><payload-type xmlns=\"\" id=\"0\" name=\"PCMU\"/></description><transport xmlns=\"urn:xmpp:jingle:transports:raw-udp:1\"><candidate xmlns=\"\" ip=\""+ip+"\" port=\"22000\" generation=\"0\"/></transport></content></jingle>";
        Document doc = DocumentHelper.parseText(packet);
        final IQ iq = new IQ(doc.getRootElement());
        iq.setFrom("romeo@localhost");
        iq.setTo("juliet@localhost");
        final JingleIQ jingleIQ = JingleIQ.fromXml(iq);
        return JingleIQ.fromXml(jingleIQ);
    }



}
