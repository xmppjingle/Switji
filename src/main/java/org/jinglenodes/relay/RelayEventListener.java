package org.jinglenodes.relay;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 5/23/12
 * Time: 4:04 PM
 */
public interface RelayEventListener {
    public void relayEventReceived(final RelayEventIQ iq);
}
