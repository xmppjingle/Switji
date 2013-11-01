package org.xmpp.component;

import junit.framework.TestCase;
import org.jinglenodes.sip.CachedSipToJingleBind;
import org.xmpp.packet.JID;

/**
 * @author bhlangonijr
 *         Date: 11/1/13
 *         Time: 11:56 AM
 */
public class CachedSipToJingleBindTest extends TestCase {


    public void testExpiration() throws InterruptedException {


        CachedSipToJingleBind cache = new CachedSipToJingleBind(100, 3 * 1000, 1000);

        for (int i=1;i<=50;i++) {
            cache.addXmppToBind(new JID("test"+i+"@sip.com"), new JID("test"+i+"@xmpp.com"));
        }

        Thread.sleep(1000);

        for (int i=1;i<=50;i++) {
            assertNotNull(cache.getXmppTo(new JID("test" + i + "@sip.com"), null));
        }

        Thread.sleep(4000);

        for (int i=1;i<=50;i++) {
            assertNull(cache.getXmppTo(new JID("test" + i + "@sip.com"), null));
        }


    }


}
