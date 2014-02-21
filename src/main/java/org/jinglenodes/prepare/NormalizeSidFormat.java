package org.jinglenodes.prepare;

import org.jinglenodes.jingle.Jingle;
import org.xmpp.tinder.JingleIQ;

/**
 * Normalizes Sid format
 *
 * This is a HACK to fix server side issue with IPHone5 client
 * sending Sid with spaces. It should be removed as soon as possible
 *
 * @author bhlangonijr
 *         Date: 2/18/14
 *         Time: 11:19 AM
 */
public class NormalizeSidFormat implements IQNormalizer {

    private final String pattern;
    private final String replace;

    public NormalizeSidFormat(String pattern, String replace) {
        this.pattern = pattern;
        this.replace = replace;
    }

    @Override
    public String getNamespace() {
        return Jingle.XMLNS;
    }

    @Override
    public JingleIQ normalize(JingleIQ iq) {

        final Jingle old = iq.getJingle();

        if (old.getSid().indexOf(getPattern()) < 0) {
            return iq;
        }

        final String newSid = old.getSid().replace(getPattern(), getReplace());

        return cloneJingle(newSid, iq);
    }

    @Override
    public JingleIQ deNormalize(JingleIQ iq) {
        final Jingle old = iq.getJingle();

        if (old.getSid().indexOf(getReplace()) < 0) {
            return iq;
        }

        final String newSid = old.getSid().replace(getReplace(), getPattern());

        return cloneJingle(newSid, iq);
    }

    private static JingleIQ cloneJingle(final String newSid, final JingleIQ old) {
        Jingle j = new Jingle(newSid, old.getJingle().getInitiator(),
                old.getJingle().getResponder(), old.getJingle().getAction());
        j.setContent(old.getJingle().getContent());
        j.setInfo(old.getJingle().getInfo());
        j.setReason(old.getJingle().getReason());

        final JingleIQ jingleIQ = new JingleIQ(j);
        jingleIQ.setTo(old.getTo());
        jingleIQ.setFrom(old.getFrom());
        jingleIQ.setID(old.getID());
        jingleIQ.setType(old.getType());
        return jingleIQ;
    }

    public String getPattern() {
        return pattern;
    }

    public String getReplace() {
        return replace;
    }
}
