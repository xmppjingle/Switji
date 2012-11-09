package org.xmpp;

import junit.framework.TestCase;
import org.jinglenodes.format.NodeFormatPreparation;
import org.jinglenodes.format.NumberFormatPreparation;
import org.jinglenodes.prepare.PrefixNodeFormat;
import org.xmpp.tinder.JingleIQ;

/**
 * Created with IntelliJ IDEA.
 * User: spider
 * Date: 11/9/12
 * Time: 10:16 AM
 * To change this template use File | Settings | File Templates.
 */
public class NodePreparationTest extends TestCase {

    final private NumberFormatPreparation nuPreparation = new NumberFormatPreparation();
    final private NodeFormatPreparation noPreparation = new NodeFormatPreparation();

    public void testCompare() {

        final PrefixNodeFormat prefixFormat = new PrefixNodeFormat();
        prefixFormat.setPrefix("");

        noPreparation.setInitiatorNodeFormat(prefixFormat);
        noPreparation.setResponderNodeFormat(prefixFormat);

        final JingleIQ nuIq = TestSIPGateway.fakeJingleInitiate("0034660555555@test.com", "+34660555444@test.com", "0034660555444@sip.test.com", "abc");
        final JingleIQ noIq = TestSIPGateway.fakeJingleInitiate("0034660555555@test.com", "+34660555444@test.com", "0034660555444@sip.test.com", "abc");

        nuPreparation.proceedInitiate(nuIq, null);
        noPreparation.proceedInitiate(noIq, null);

        assertEquals(nuIq.getJingle().getInitiator(), noIq.getJingle().getInitiator());
        assertEquals(nuIq.getJingle().getResponder(), noIq.getJingle().getResponder());

        // Test No Overlaps
        nuPreparation.proceedInitiate(nuIq, null);
        noPreparation.proceedInitiate(noIq, null);

        assertEquals(nuIq.getJingle().getInitiator(), noIq.getJingle().getInitiator());
        assertEquals(nuIq.getJingle().getResponder(), noIq.getJingle().getResponder());

        System.out.println(noIq.getJingle().getInitiator());

    }

}
