/*
 * Copyright (C) 2011 - Jingle Nodes - Yuilop - Neppo
 *
 *   This file is part of Switji (http://jinglenodes.org)
 *
 *   Switji is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   Switji is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with MjSip; if not, write to the Free Software
 *   Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 *   Author(s):
 *   Benhur Langoni (bhlangonijr@gmail.com)
 *   Thiago Camargo (barata7@gmail.com)
 */

package org.jinglenodes.sip.router;

import org.apache.log4j.Logger;
import org.zoolu.sip.message.SipChannel;

import java.io.UnsupportedEncodingException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class SipMessageFilter implements SipPacketFilter {

    private static final Logger log = Logger.getLogger(SipMessageFilter.class);
    final String[] allowedKeys = new String[]{"SIP/2.0", "INVITE", "REGISTER", "MESSAGE", "CANCEL", "BYE", "OPTIONS", "NOTIFY", "SUBSCRIBE", "ACK", "INFO", "PUBLISH"};
    public static final String ENCODE = "UTF-8";

    public boolean acceptPacket(ByteBuffer byteBuffer, SocketAddress address, SipChannel channel) {


        final String str;
        try {

            final int max = 10;

            int size = byteBuffer.position();

            if (size < 3) {
                return false;
            }

            byteBuffer.rewind();
            byte o[] = new byte[max];
            byteBuffer.get(o, 0, size > max ? max : size);
            int a = getTrimOffset(o, max);
            byte b[] = Arrays.copyOfRange(o, 0, size);

            if (a >= size) return false;

            str = new String(b, a, size, ENCODE);

            for (final String allowed : allowedKeys) {
                if (str.startsWith(allowed)) {
                    return true;
                }
            }

        } catch (UnsupportedEncodingException e) {
            log.debug("Dropped Invalid UDP Packet", e);
            return false;
        } catch (Throwable e) {
            log.debug("Dropped Invalid Packet", e);
            return false;
        }

        log.debug("Dropped Invalid SIP Packet");
        return false;

    }

    public static int getTrimOffset(final byte[] s, final int maxPos) {
        int i;
        //noinspection StatementWithEmptyBody
        for (i = 0; i < maxPos && (s[i] == '\n' || s[i] == '\r' || s[i] == ' ' || s[i] == '\t'); i++) ;
        return i;
    }

}
