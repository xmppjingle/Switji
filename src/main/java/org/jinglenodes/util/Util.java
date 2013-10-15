package org.jinglenodes.util;

/**
 * @author bhlangonijr
 *         Date: 10/15/13
 *         Time: 4:33 PM
 */
public class Util {

    public static boolean isNumeric(String str) {
        boolean result = true;
        try {
            Double.parseDouble(str);
        } catch(NumberFormatException nfe) {
            result = false;
        }
        return result;
    }


    private Util() {};
}
