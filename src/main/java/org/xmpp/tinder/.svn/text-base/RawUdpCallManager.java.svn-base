package org.minijingle.xmpp.smack;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;
import org.minijingle.jingle.Jingle;
import org.minijingle.jingle.content.Content;
import org.minijingle.jingle.description.Description;
import org.minijingle.jingle.description.Payload;
import org.minijingle.jingle.transport.Candidate;
import org.minijingle.jingle.transport.RawUdpTransport;
import org.minijingle.media.MediaManager;

public class RawUdpCallManager implements PacketListener {

    private Candidate localCandidate;
    private Description localDescription;
    private Candidate remoteCandidate;
    private String sid;
    private final MediaManager mediaManager;
    private final RawUdpTransport transport;
    private final XMPPConnection connection;
    private boolean inACall = false;
    private final String proxy;

    public RawUdpCallManager(final XMPPConnection connection, final MediaManager mediaManager, final RawUdpTransport transport, final String proxy) {
        this.connection = connection;
        this.mediaManager = mediaManager;
        this.proxy = proxy;
        this.transport = transport;
        this.localDescription = new Description("audio");
        for (final Payload payload : mediaManager.getPayloads()) {
            this.localDescription.addPayload(payload);
        }
        this.connection.addPacketListener(this, new PacketFilter() {
            public boolean accept(Packet packet) {
                return true;
            }
        });
    }

    public void processPacket(final Packet packet) {

        if (packet instanceof JingleIQ) {
            processJingle((JingleIQ) packet);
        }

    }

    private void processJingle(final JingleIQ jingleIQ) {

        connection.sendPacket(JingleIQ.createResult(jingleIQ));

        final Jingle jingle = jingleIQ.getElement();

        // Incomming Call
        if (Jingle.SESSION_INITIATE.equals(jingle.getAction())) {
            if (sid == null && !inACall) {
                //Auto Accept
                acceptCall(jingle);
            }
        }
        // Call Accepted
        else if (Jingle.SESSION_ACCEPT.equals(jingle.getAction())) {
            if (!inACall) {
                callAccepted(jingle);
            }
        }
        // Call Terminated
        else if (Jingle.SESSION_TERMINATE.equals(jingle.getAction())) {
            if (sid != null) {
                terminateCall(jingle);
            }
        }

    }

    private void callAccepted(final Jingle jingle) {

        if (jingle.getContent().getTransport().getCandidates().size() > 0
                && jingle.getContent().getDescription().getPayloads().size() > 0) {

            inACall = true;
            remoteCandidate = jingle.getContent().getTransport().getCandidates().get(0);
            mediaManager.startMedia(localCandidate, remoteCandidate, mediaManager.getPayloads().get(0));
        }

    }

    private void terminateCall(final Jingle jingle) {

        mediaManager.stopMedia();
        inACall = false;
        sid = null;

    }

    private void acceptCall(final Jingle jingle) {

        if (jingle.getContent().getTransport().getCandidates().size() > 0
                && jingle.getContent().getDescription().getPayloads().size() > 0) {

            inACall = true;
            sid = jingle.getSid();
            remoteCandidate = jingle.getContent().getTransport().getCandidates().get(0);
            localCandidate = transport.getCandidates().get(0);

            final Content localContent = new Content(jingle.getContent().getCreator(), connection.getUser().split("/")[0], "both", localDescription, transport);
            final Jingle accept = new Jingle(sid, jingle.getInitiator(), this.connection.getUser(), Jingle.SESSION_ACCEPT);
            accept.setContent(localContent);

            final JingleIQ acceptIQ = new JingleIQ(accept);
            acceptIQ.setFrom(connection.getUser());

            if (proxy != null) {
                acceptIQ.setTo(proxy);
            } else {
                acceptIQ.setTo(jingle.getInitiator());
            }

            System.out.println("Sent: " + acceptIQ.toXML());

            mediaManager.startMedia(localCandidate, remoteCandidate, localDescription.getPayloads().get(0));

            connection.sendPacket(acceptIQ);

        }

    }

}
