package org.minijingle.jingle.transport;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("transport")
public class RawUdpTransport{

    @XStreamAsAttribute
    @XStreamAlias("xmlns")
    public final String NAMESPACE = "urn:xmpp:tmp:jingle:transports:raw-udp";

    @XStreamImplicit
    @XStreamAlias("candidate")
    private final ArrayList<Candidate> candidates = new ArrayList<Candidate>();

    public RawUdpTransport(Candidate candidate) {
        this.candidates.add(candidate);
    }

    public List<Candidate> getCandidates() {
        return candidates;
    }
}
