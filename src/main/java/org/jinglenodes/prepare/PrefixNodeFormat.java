package org.jinglenodes.prepare;

/**
 * Created with IntelliJ IDEA.
 * User: thiago
 * Date: 9/5/12
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class PrefixNodeFormat implements NodeFormat {
    private String prefix = "+";

    @Override
    public String formatNode(final String node, final String reference) {
        if (node == null) return node;

        String nnode;
        if (node.indexOf("00") == 0) {
            nnode = prefix + node.substring(2);
        } else if (node.charAt(0) == '+') {
            nnode = prefix + node.substring(1);
        } else if (node.startsWith(prefix) ||
                !isNumeric(node)) {
            nnode = node;
        } else {
            nnode = prefix + node;
        }
        return nnode;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public static boolean isNumeric(String str) {
        boolean result = true;
        try {
            Double.parseDouble(str);
        } catch(NumberFormatException nfe) {
            result = false;
        }
        return result;
    }

}
