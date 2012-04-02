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

import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Utilty class to perform DNS lookups for SIP services.
 *
 * @author Thiago Camargo - Jingle Nodes
 */
public class DNSUtil {

    private static DirContext context;

    static {
        try {
            final Hashtable<String, String> env = new Hashtable<String, String>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            context = new InitialDirContext(env);
        } catch (Exception e) {
            //Do Nothing
        }
    }

    /**
     * Performs SRV lookup.
     * For "example.com" may return "sip.example.com:5060".
     *
     * @param domain      the domain.
     * @param defaultPort default port to return if the DNS look up fails.
     * @return a HostAddress, which encompasses the hostname and port that the SIP
     *         server can be reached at for the specified domain.
     */
    public static HostAddress resolveSIPServerDomain(final String domain, final int defaultPort) {
        if (context == null) {
            return new HostAddress(domain, defaultPort);
        }
        String host = domain;
        int port = defaultPort;
        try {
            final Attributes dnsLookup =
                    context.getAttributes("_sip._udp." + domain, new String[]{"SRV"});
            final String srvRecord = (String) dnsLookup.get("SRV").get();
            final String[] srvRecordEntries = srvRecord.split(" ");
            port = Integer.parseInt(srvRecordEntries[srvRecordEntries.length - 2]);
            host = srvRecordEntries[srvRecordEntries.length - 1];
        } catch (Exception e) {
            // Entries Not Found
        }
        // Host entries in DNS should end with a ".".
        if (host.endsWith(".")) {
            host = host.substring(0, host.length() - 1);
        }
        return new HostAddress(host, port);
    }

    private static String encode(final Map<String, HostAddress> internalDNS) {
        if (internalDNS == null) {
            return "";
        }
        final StringBuilder sb = new StringBuilder(100);
        for (final String key : internalDNS.keySet()) {
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append("{").append(key).append(",");
            sb.append(internalDNS.get(key).getHost()).append(":");
            sb.append(internalDNS.get(key).getPort()).append("}");
        }
        return sb.toString();
    }

    private static Map<String, HostAddress> decode(final String encodedValue) {
        final Map<String, HostAddress> answer = new HashMap<String, HostAddress>();
        final StringTokenizer st = new StringTokenizer(encodedValue, "{},:");
        while (st.hasMoreElements()) {
            final String key = st.nextToken();
            answer.put(key, new HostAddress(st.nextToken(), Integer.parseInt(st.nextToken())));
        }
        return answer;
    }

    /**
     * Encapsulates a hostname and port.
     */
    public static class HostAddress {

        private final String host;
        private final int port;

        private HostAddress(final String host, final int port) {
            this.host = host;
            this.port = port;
        }

        /**
         * Returns the hostname.
         *
         * @return the hostname.
         */
        public String getHost() {
            return host;
        }

        /**
         * Returns the port.
         *
         * @return the port.
         */
        public int getPort() {
            return port;
        }

        public String toString() {
            return host + ":" + port;
        }
    }
}