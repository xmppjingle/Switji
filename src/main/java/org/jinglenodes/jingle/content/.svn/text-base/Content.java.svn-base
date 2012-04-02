package org.minijingle.jingle.content;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.minijingle.jingle.description.Description;
import org.minijingle.jingle.transport.RawUdpTransport;

@XStreamAlias("content")
public class Content {

    @XStreamAsAttribute
    private String creator, name, senders;

    private Description description;
    private RawUdpTransport transport;

    public Content(String creator, String name, String senders, Description description, RawUdpTransport transport) {
        this.creator = creator;
        this.name = name;
        this.senders= senders;
        this.description = description;
        this.transport = transport;
    }

    public String getCreator() {
        return creator;
    }

    public String getName() {
        return name;
    }

    public String getSenders() {
        return senders;
    }

    public Description getDescription() {
        return description;
    }

    public RawUdpTransport getTransport() {
        return transport;
    }
}
