package org.minijingle.jingle.description;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("description")
public class Description {

    @XStreamAsAttribute
    @XStreamAlias("xmlns")
    public final String NAMESPACE = "urn:xmpp:tmp:jingle:apps:rtp";

    @XStreamAsAttribute
    private final String media;

    @XStreamImplicit
    @XStreamAlias("payload-type")
    private final List<Payload> payloads = new ArrayList<Payload>();

    public Description(String media) {
        this.media = media;
    }

    public void addPayload(final Payload payload){
        payloads.add(payload);
    }

    public List<Payload> getPayloads() {
        return payloads;
    }

    public String getMedia() {
        return media;
    }
}

