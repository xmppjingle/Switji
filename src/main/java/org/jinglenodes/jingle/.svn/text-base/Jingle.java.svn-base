package org.minijingle.jingle;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import org.minijingle.jingle.content.Content;
import org.minijingle.xmpp.smack.parser.XStreamIQ;

@XStreamAlias("jingle")
public class Jingle {

    public final static String SESSION_INITIATE = "session-initiate";
    public final static String SESSION_TERMINATE = "session-terminate";
    public final static String SESSION_ACCEPT = "session-accept";

    @XStreamAsAttribute
    @XStreamAlias("xmlns")
    public final String NAMESPACE = "urn:xmpp:tmp:jingle";
    public static final String XMLNS = "urn:xmpp:tmp:jingle";

    @XStreamAsAttribute
    private String action, sid, initiator, responder;

    private Content content;

    public Jingle(String sid, String initiator, String responder, String action) {
        this.sid = sid;
        this.initiator = initiator;
        this.responder = responder;
        this.action = action;
    }

    public void setContent(Content content) {
        this.content = content;
    }

    public Content getContent() {
        return content;
    }

    public String getSid() {
        return sid;
    }

    public String getInitiator() {
        return initiator;
    }

    public String getResponder() {
        return responder;
    }

    public String getAction() {
        return action;
    }

    public String toString() {
        return XStreamIQ.getStream().toXML(this);
    }

}
