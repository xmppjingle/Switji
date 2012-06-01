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

package org.jinglenodes.sip.account;


import org.xmpp.packet.JID;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Cached Enabled SipAccountProvider Implementation.
 */
public class CachedSipAccountProvider implements SipAccountProvider {

    private final ConcurrentHashMap<String, SipAccount> cachedAccounts = new ConcurrentHashMap<String, SipAccount>();

    /**
     * Get SipAccount. First Check in the cached Account. If present return it. if NOT request
     * the main SipAccountProvider, stores in the cache and return it.
     * If no account present returns null;
     *
     * @param userJID username (key)
     * @return Cached SipAccount or null if none found.
     */
    public SipAccount getSipAccount(final JID userJID) {

        if (userJID == null) {
            return null;
        }
        final String user = userJID.toBareJID();

        if (cachedAccounts.containsKey(user)) {
            return cachedAccounts.get(user);
        }

        return null;
    }

    public SipAccount removeSipAccount(final JID userJID) {
        final String user = userJID.toBareJID();
        cachedAccounts.remove(user);
        return getSipAccount(userJID);
    }

    public void addSipAccount(final JID userJID, final SipAccount sipAccount) {
        cachedAccounts.put(userJID.toBareJID(), sipAccount);
    }

    public long getCachedAccountsSize() {
        return cachedAccounts.size();
    }

    public void clearCachedAccounts(){
        cachedAccounts.clear();
    }
}
