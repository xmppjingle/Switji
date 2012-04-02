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

/**
 * Interface Responsible to provide User SIP account details. It MUST always return reliable values, even after user is registered. As the
 * supported SIP transport protocol is UDP based the SIP Server MIGHT request for AUTHENTICATIONs before almost every new Requests
 * including: REGISTER, INVITE and MESSAGE.
 *
 * @author Thiago Rocha Camargo (thiago@jinglenodes.org) - Jingle Nodes
 *         Date: Dec 20, 2011
 *         Time: 10:08 AM
 */
public interface SipAccountProvider {

    public SipAccount getSipAccount(JID user);

}
