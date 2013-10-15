package org.jinglenodes.prepare;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import org.apache.log4j.Logger;
import org.jinglenodes.util.Util;

/**
 * Created with IntelliJ IDEA.
 * User: thiago
 * Date: 9/5/12
 * Time: 11:09 AM
 * To change this template use File | Settings | File Templates.
 */
public class E164NodeFormat implements NodeFormat {
    private static final Logger log = Logger.getLogger(E164NodeFormat.class);

    @Override
    public String formatNode(final String node, final String reference) {
        String nnode = node;

        if (Util.isNumeric(node.replace("+",""))) {

            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            Phonenumber.PhoneNumber toNumberProto = null;

            if (node.indexOf("00") == 0) {
                nnode = "+" + node.substring(2);
            } else if(nnode.indexOf("+") < 0){
                nnode = "+" + node;
            }

            try {
                toNumberProto = phoneUtil.parse(nnode, "EN");
            } catch (NumberParseException e) {
                log.error("NumberParseException was thrown: " + node, e);
            }
            if (null != toNumberProto && phoneUtil.isValidNumber(toNumberProto)) {
                nnode = phoneUtil.format(toNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
            }

        }
        return nnode;
    }

}
