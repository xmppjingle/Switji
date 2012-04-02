package org.minijingle.xmpp.smack;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.Roster;


public abstract class Client {

    protected final XMPPConnection connection;
    private final String username, password;

    public Client(String username, String password, String server) {
        this.username = username;
        this.password = password;
        ConnectionConfiguration conf = new ConnectionConfiguration(server, 5222);
        conf.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        conf.setSASLAuthenticationEnabled(false);
        connection = new XMPPConnection(conf);
    }

    protected void login() {

        try {
            connection.connect();
            connection.login(username, password);
            connection.getRoster().setSubscriptionMode(Roster.SubscriptionMode.accept_all);

            loggedIn();
        }
        catch (XMPPException e) {
            e.printStackTrace();
            loggedOut();
        }

    }

    protected abstract void loggedIn();

    protected abstract void loggedOut();
}
