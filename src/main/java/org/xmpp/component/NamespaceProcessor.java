package org.xmpp.component;

import org.xmpp.packet.IQ;

/**
 * Created by IntelliJ IDEA.
 * User: Thiago
 * Date: 09/02/12
 * Time: 12:01
 */

public interface NamespaceProcessor {
    public IQ processIQGet(final IQ iq);

    public IQ processIQSet(final IQ iq);

    public void processIQError(final IQ iq);

    public void processIQResult(final IQ iq);

    public String getNamespace();
}
