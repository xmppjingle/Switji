package org.jinglenodes.prepare;

import org.xmpp.tinder.JingleIQ;

/**
 * Sid format
 */
public interface IQNormalizer {

    public JingleIQ normalize(JingleIQ iq);

}
