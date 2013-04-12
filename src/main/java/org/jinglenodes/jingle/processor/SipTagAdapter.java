package org.jinglenodes.jingle.processor;

import org.xmpp.packet.JID;

/**
 * Created with IntelliJ IDEA.
 * User: spider
 * Date: 4/11/13
 * Time: 4:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SipTagAdapter {

    public String getTagFromJID(final JID jid);

}
