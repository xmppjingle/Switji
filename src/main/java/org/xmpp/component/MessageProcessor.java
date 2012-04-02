package org.xmpp.component;

import org.xmpp.packet.Message;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 2/20/12
 * Time: 10:28 AM
 * To change this template use File | Settings | File Templates.
 */
public interface MessageProcessor {
    public void processMessage(final Message message) throws ServiceException;
}
