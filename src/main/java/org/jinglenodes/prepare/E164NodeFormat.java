package org.jinglenodes.prepare;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.xmpp.packet.IQ;

/**
 * Created with IntelliJ IDEA.
 * User: thiago
 * Date: 9/5/12
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class E164NodeFormat implements NodeFormat {

    @Override
    public String formatNode(final String node, final String reference) {
        String nnode = node;
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber toNumberProto = null;
        if (node.indexOf("00") == 0) {
            nnode = "+" + node.substring(2);
        }
        try {
            toNumberProto = phoneUtil.parse(node, "EN");
        } catch (NumberParseException e) {
            System.err.println("NumberParseException was thrown: " + e.toString());
        }
        if (null != toNumberProto && phoneUtil.isValidNumber(toNumberProto)) {
            nnode = phoneUtil.format(toNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
        }
        return nnode;
    }

}
