package org.minijingle.xmpp.smack;

import org.minijingle.jingle.description.Payload;
import org.minijingle.jingle.transport.Candidate;
import org.minijingle.jingle.transport.RawUdpTransport;
import org.minijingle.media.MediaManager;
import org.minijingle.network.LocalIPResolver;

import java.util.List;

public class RawUdpJingleClient extends Client {

    private RawUdpCallManager callManager;
    private final String jingleProxy;

    public RawUdpJingleClient(final String username, final String password, final String server, final String jingleProxy) {
        super(username, password, server);
        this.jingleProxy = jingleProxy;
        login();
    }

    protected void loggedIn() {

        JingleProvider.enableJingle(connection);

        this.callManager = new RawUdpCallManager(connection, new MediaManager() {
            public void startMedia(Candidate local, Candidate remote, Payload payload) {

            }

            public void stopMedia() {

            }

            public List<Payload> getPayloads() {
                return null;
            }
        }, new RawUdpTransport(new Candidate(LocalIPResolver.getLocalIP(), "10002", "0")), jingleProxy);

    }

    protected void loggedOut() {

    }

    public static void main(String args[]) {

        new RawUdpJingleClient(args[1], args[2], args[3], args[4]);

        for (int i = 0; i < 1000; i++)
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


    }

}
