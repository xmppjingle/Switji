package org.jinglenodes.log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.jinglenodes.jingle.Info;
import org.jinglenodes.jingle.Jingle;
import org.jinglenodes.jingle.Reason;
import org.jinglenodes.jingle.transport.Candidate;
import org.jinglenodes.jingle.transport.RawUdpTransport;
import org.jinglenodes.prepare.CallPreparation;
import org.jinglenodes.session.CallSession;
import org.jinglenodes.session.persistence.redis.JedisConnection;
import org.xmpp.tinder.JingleIQ;
import org.xmpp.util.NamedThreadFactory;
import org.zoolu.sip.message.Message;
import org.zoolu.sip.message.SipChannel;
import redis.clients.jedis.Jedis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Logs information into a redis channel
 * @author benhur
 *
 */
public class RedisPubSubLogPreparation extends CallPreparation {
    final private static Logger log = Logger.getLogger(RedisPubSubLogPreparation.class);
    final static public String DEFAULT_BLANK = "-";
    final static public String DEFAULT_UNKNOWN = "unknown";
    private String redisHost = "localhost";
    private int redisPort = 6379;
    private String channel = "sipgateway.log";
    private ExecutorService service = Executors.newSingleThreadExecutor(new NamedThreadFactory("RedisPubSub"));


    private final ThreadLocal<Gson> gson = new ThreadLocal<Gson>() {
        @Override
        protected Gson initialValue() {
            return new GsonBuilder().setPrettyPrinting().create();

        }
    };

    @Override
    public boolean prepareInitiate(JingleIQ iq, CallSession session) {
        return true;
    }

    @Override
    public boolean proceedInitiate(JingleIQ iq, CallSession session) {
        publishLogEntry(iq, session);
        return true;
    }

    private String getReason(final JingleIQ iq) {
        if (iq.getJingle() != null && iq.getJingle().getReason() != null) {
            final Reason.Type t = iq.getJingle().getReason().getType();
            return t == null ? DEFAULT_BLANK : t.toString();
        } else if (iq.getJingle() != null && iq.getJingle().getInfo() != null) {
            final Info.Type info = iq.getJingle().getInfo().getType();
            return info == null ? DEFAULT_BLANK : info.toString();
        }
        return DEFAULT_BLANK;
    }

    private String getIp(final JingleIQ iq) {
        if (iq.getJingle() != null && iq.getJingle().getContent() != null) {
            final RawUdpTransport transport = iq.getJingle().getContent().getTransport();
            if (transport != null && transport.getCandidates().size() > 0) {
                final Candidate c = transport.getCandidates().get(0);
                if ("srflx".equals(c.getType())) {
                    return c.getIp();
                }
            }
        }
        return DEFAULT_UNKNOWN;
    }

    @Override
    public boolean proceedTerminate(JingleIQ iq, CallSession session) {
        publishLogEntry(iq, session);
        return true;
    }

    @Override
    public boolean proceedAccept(JingleIQ iq, CallSession session) {
        publishLogEntry(iq, session);
        return true;
    }

    @Override
    public void proceedInfo(JingleIQ iq, CallSession session) {
        publishLogEntry(iq, session);
    }

    private LogEntry publishLogEntry(final JingleIQ iq, final CallSession session) {
        final Jingle j = iq.getJingle();
        return publishLogEntry(j.getAction(), j.getSid(), getReason(iq), j.getInitiator(),
                j.getResponder(), getIp(iq), _getElapsed(session), iq.toXML());
    }

    private String _getElapsed(CallSession session) {
        if (session != null) {
            try {
                return String.valueOf(System.currentTimeMillis() - session.getCreationTime());
            } catch (Throwable t) {
                // Do Nothing
            }
        }
        return "-";
    }

    private LogEntry publishLogEntry(final String action, final String sid, final String reasonType,
                                     final String initiator, final String responder, final String ip,
                                     final String elapsed, final String payload) {
        final LogEntry entry = new LogEntry();
        entry.setAction(action);
        entry.setSid(sid);
        entry.setInitiator(initiator);
        entry.setResponder(responder);
        entry.setReasonType(reasonType);
        entry.setIp(ip);
        entry.setElapsed(elapsed);
        entry.setPayload(payload);

        service.submit(new Runnable() {
            @Override
            public void run() {
                sendToRedis(entry);
            }
        });

        return entry;
    }

    private void sendToRedis(LogEntry entry) {

        try {

            final JedisConnection connection = JedisConnection.getInstance(redisHost, redisPort);
            final Jedis jedis = connection.getResource();

            final String json = gson.get().toJson(entry);
            jedis.publish(getChannel(), json);

        } catch (Exception e) {
            log.error("Could not push event to Redis channel: ", e);
        }


    }

    @Override
    public boolean prepareInitiate(Message msg, CallSession session, SipChannel channel) {
        return true;
    }

    @Override
    public JingleIQ proceedSIPInitiate(JingleIQ iq, CallSession session, SipChannel channel) {
        publishLogEntry(iq, session);
        return iq;
    }

    @Override
    public void proceedSIPInfo(JingleIQ iq, CallSession session, SipChannel channel) {
        publishLogEntry(iq, session);
    }

    @Override
    public JingleIQ proceedSIPEarlyMedia(JingleIQ iq, CallSession session, SipChannel channel) {
        return iq;
    }

    @Override
    public JingleIQ proceedSIPTerminate(JingleIQ iq, CallSession session, SipChannel channel) {
        publishLogEntry(iq, session);
        return iq;
    }

    @Override
    public JingleIQ proceedSIPAccept(JingleIQ iq, CallSession session, SipChannel channel) {
        publishLogEntry(iq, session);
        return iq;
    }

    public String getRedisHost() {
        return redisHost;
    }

    public void setRedisHost(String redisHost) {
        this.redisHost = redisHost;
    }

    public int getRedisPort() {
        return redisPort;
    }

    public void setRedisPort(int redisPort) {
        this.redisPort = redisPort;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
