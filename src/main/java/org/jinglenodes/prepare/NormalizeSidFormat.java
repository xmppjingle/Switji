package org.jinglenodes.prepare;

import org.jinglenodes.jingle.Jingle;
import org.xmpp.tinder.JingleIQ;

/**
 * Normalizes Sid format
 *
 * @author bhlangonijr
 *         Date: 2/18/14
 *         Time: 11:19 AM
 */
public class NormalizeSidFormat implements IQNormalizer {
    @Override
    public JingleIQ normalize(JingleIQ iq) {

        final Jingle old = iq.getJingle();

        if (old.getSid().indexOf('_') < 0) {
            return iq;
        }

        final String newSid = old.getSid().replace("_", "");

        Jingle j = new Jingle(newSid, old.getInitiator(), old.getResponder(), old.getAction());
        j.setContent(old.getContent());
        j.setInfo(old.getInfo());
        j.setReason(old.getReason());

        final JingleIQ jingleIQ = new JingleIQ(j);
        jingleIQ.setTo(iq.getTo());
        jingleIQ.setFrom(iq.getFrom());
        jingleIQ.setID(iq.getID());
        jingleIQ.setType(iq.getType());

        return jingleIQ;
    }
}
