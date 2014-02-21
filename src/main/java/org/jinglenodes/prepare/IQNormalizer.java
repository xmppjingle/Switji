package org.jinglenodes.prepare;

import org.xmpp.tinder.JingleIQ;

/**
 * Sid format
 */
public interface IQNormalizer {

    public String getNamespace();

    public JingleIQ normalize(JingleIQ iq);

    public JingleIQ deNormalize(JingleIQ iq);

}
