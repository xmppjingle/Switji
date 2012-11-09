package org.jinglenodes.prepare;

import org.xmpp.packet.IQ;

/**
 * Created with IntelliJ IDEA.
 * User: thiago
 * Date: 9/5/12
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class E164NodeFormat implements NodeFormat {

    private String prefix = "+";

    @Override
    public String formatNode(final String node) {
        String nnode;
        if (node.indexOf("00") == 0) {
            nnode = prefix + node.substring(2);
        } else if (node.charAt(0) != '+') {
            nnode = prefix + node;
        } else {
            nnode = node;
        }
        return nnode;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}
