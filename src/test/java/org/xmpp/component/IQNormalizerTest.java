package org.xmpp.component;

import junit.framework.TestCase;
import org.jinglenodes.jingle.Jingle;
import org.jinglenodes.jingle.content.Content;
import org.jinglenodes.jingle.description.Description;
import org.jinglenodes.jingle.description.Payload;
import org.jinglenodes.jingle.transport.Candidate;
import org.jinglenodes.jingle.transport.RawUdpTransport;
import org.jinglenodes.prepare.NormalizeSidFormat;
import org.xmpp.tinder.JingleIQ;

/**
 * @author bhlangonijr
 *         Date: 2/18/14
 *         Time: 11:28 AM
 */
public class IQNormalizerTest extends TestCase {


    public void testSidNormalization() {

        NormalizeSidFormat format = new NormalizeSidFormat();

        String sid = "I5utessainsx140218011514_pm674";

        JingleIQ iq = fakeJingleInitiate("initiator@abc.com", "responder@abc.com", "sip.abc.com", sid);

        JingleIQ iqNormalized = format.normalize(iq);

        assertEquals("I5utessainsx140218011514pm674", iqNormalized.getJingle().getSid());

    }


    public void testSidNormalizationNoChange() {

        NormalizeSidFormat format = new NormalizeSidFormat();

        String sid = "A5utessainsx140218011514pm673";

        JingleIQ iq = fakeJingleInitiate("initiator@abc.com", "responder@abc.com", "sip.abc.com", sid);

        JingleIQ iqNormalized = format.normalize(iq);

        assertEquals("A5utessainsx140218011514pm673", iqNormalized.getJingle().getSid());

    }


    public static JingleIQ fakeJingleInitiate(final String initiator, final String responder, final String to, final String sid) {
        final Jingle jingle = new Jingle(sid, initiator, responder, Jingle.SESSION_INITIATE);
        jingle.setContent(new Content("initiator", "audio", "both", new Description("audio"), new RawUdpTransport(new Candidate("10.166.108.22", "10000", "0"))));
        jingle.getContent().getDescription().addPayload(Payload.G729);
        jingle.getContent().getDescription().addPayload(Payload.TELEPHONE_EVENT);
        final JingleIQ jingleIQ = new JingleIQ(jingle);
        jingleIQ.setTo(to);
        jingleIQ.setFrom(initiator);
        return jingleIQ;
    }
}
