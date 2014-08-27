package org.jinglenodes.sip;

import org.jinglenodes.jingle.Reason;
import org.zoolu.sip.message.Message;

import java.util.HashMap;
import java.util.Map;

/**
 * @author bhlangonijr
 *         Date: 12/9/13
 *         Time: 10:46 AM
 */
public class SipToJingleCodes {

    private static final Map<Integer, Reason.Type> reasonMap = new HashMap<Integer, Reason.Type>();

    static {
        reasonMap.put(200, Reason.Type.success);
        reasonMap.put(202, Reason.Type.success);
        reasonMap.put(204, Reason.Type.success);

        reasonMap.put(400, Reason.Type.general_error);
        reasonMap.put(401, Reason.Type.security_error);
        reasonMap.put(402, Reason.Type.payment);
        reasonMap.put(403, Reason.Type.forbidden);
        reasonMap.put(404, Reason.Type.connectivity_error);
        reasonMap.put(405, Reason.Type.forbidden);

        reasonMap.put(406, Reason.Type.general_error);
        reasonMap.put(407, Reason.Type.security_error);
        reasonMap.put(408, Reason.Type.general_error);
        reasonMap.put(409, Reason.Type.general_error);
        reasonMap.put(410, Reason.Type.gone);
        reasonMap.put(411, Reason.Type.general_error);
        reasonMap.put(412, Reason.Type.general_error);
        reasonMap.put(413, Reason.Type.general_error);

        reasonMap.put(414, Reason.Type.connectivity_error);
        reasonMap.put(415, Reason.Type.media_error);

        reasonMap.put(416, Reason.Type.general_error);
        reasonMap.put(417, Reason.Type.general_error);
        reasonMap.put(420, Reason.Type.connectivity_error);

        reasonMap.put(421, Reason.Type.general_error);
        reasonMap.put(422, Reason.Type.general_error);
        reasonMap.put(423, Reason.Type.general_error);
        reasonMap.put(424, Reason.Type.general_error);

        reasonMap.put(480, Reason.Type.busy);
        reasonMap.put(481, Reason.Type.connectivity_error);
        reasonMap.put(482, Reason.Type.connectivity_error);
        reasonMap.put(483, Reason.Type.connectivity_error);
        reasonMap.put(484, Reason.Type.connectivity_error);
        reasonMap.put(485, Reason.Type.connectivity_error);
        reasonMap.put(486, Reason.Type.busy);

        reasonMap.put(487, Reason.Type.connectivity_error);
        reasonMap.put(488, Reason.Type.connectivity_error);
        reasonMap.put(489, Reason.Type.connectivity_error);
        reasonMap.put(490, Reason.Type.connectivity_error);
        reasonMap.put(491, Reason.Type.connectivity_error);
        reasonMap.put(492, Reason.Type.connectivity_error);
        reasonMap.put(493, Reason.Type.connectivity_error);
        reasonMap.put(494, Reason.Type.connectivity_error);

        reasonMap.put(500, Reason.Type.connectivity_error);
        reasonMap.put(501, Reason.Type.general_error);
        reasonMap.put(502, Reason.Type.connectivity_error);
        reasonMap.put(503, Reason.Type.connectivity_error);
        reasonMap.put(504, Reason.Type.connectivity_error);
        reasonMap.put(505, Reason.Type.general_error);
        reasonMap.put(513, Reason.Type.general_error);
        reasonMap.put(580, Reason.Type.connectivity_error);

        reasonMap.put(602, Reason.Type.decline);
        reasonMap.put(603, Reason.Type.decline);
        reasonMap.put(604, Reason.Type.decline);
        reasonMap.put(606, Reason.Type.decline);

    }

    private static Reason createReason(final Message msg, int code, Reason.Type type) {
        final String text = code > 0 ? (code) + " - " + msg.getStatusLine().getReason() : String.valueOf(code);
        return new Reason(text, type);

    }

    /**
     * Return a Jingle Reason based on a SIP response code
     *
     * @param msg SIP Message
     * @param code SIP code
     * @return
     */
    public static Reason getReason(final Message msg, final int code) {

        final Reason.Type type = reasonMap.get(code);

        final Reason reason = type == null ? createReason(msg, code, Reason.Type.success) :
                createReason(msg, code, type);

        return reason;
    }


    private SipToJingleCodes() {};

}
