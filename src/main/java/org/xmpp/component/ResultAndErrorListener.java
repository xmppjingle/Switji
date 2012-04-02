package org.xmpp.component;

import org.xmpp.packet.IQ;

public interface ResultAndErrorListener {
    public void handleIQError(final IQ iq);

    public void handleIQResult(final IQ iq);
}
