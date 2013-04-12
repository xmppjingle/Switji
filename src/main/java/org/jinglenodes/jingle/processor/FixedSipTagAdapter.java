package org.jinglenodes.jingle.processor;

import org.xmpp.packet.JID;

/**
 * Created with IntelliJ IDEA.
 * User: spider
 * Date: 4/11/13
 * Time: 5:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class FixedSipTagAdapter implements SipTagAdapter{

    private String fixedTag = "switji";

    @Override
    public String getTagFromJID(JID jid) {
        return fixedTag;
    }

    public String getFixedTag() {
        return fixedTag;
    }

    public void setFixedTag(String fixedTag) {
        this.fixedTag = fixedTag;
    }
}
