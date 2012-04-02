package org.minijingle.jingle.transport;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("candidate")
public class Candidate {

    @XStreamAsAttribute
    private final String ip, port, generation;

    public Candidate(String ip, String port, String generation) {
        this.ip = ip;
        this.port = port;
        this.generation = generation;
    }

    public String getIp() {
        return ip;
    }

    public String getPort() {
        return port;
    }

    public String getGeneration() {
        return generation;
    }
}
