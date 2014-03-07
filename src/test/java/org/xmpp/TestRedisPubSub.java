package org.xmpp;

import junit.framework.TestCase;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.jinglenodes.jingle.Jingle;
import org.jinglenodes.jingle.Reason;
import org.jinglenodes.log.RedisPubSubLogPreparation;
import org.jinglenodes.session.CallSession;
import org.jinglenodes.session.persistence.redis.JedisConnection;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.tinder.JingleIQ;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author bhlangonijr
 *         Date: 3/6/14
 *         Time: 9:36 PM
 */
public class TestRedisPubSub extends TestCase {

    final static RedisPubSubLogPreparation preparation;
    final static ExecutorService service;
    final static ArrayBlockingQueue<String> queue;

    static {

        service = Executors.newSingleThreadExecutor();
        preparation = new RedisPubSubLogPreparation();
        queue = new ArrayBlockingQueue<String>(10);

        final JedisConnection connection = JedisConnection.getInstance("localhost", 6379);
        final Jedis jedis = connection.getResource();

        final MyListener l = new MyListener();

        service.execute(new Runnable() {
            @Override
            public void run() {
                jedis.subscribe(l, preparation.getChannel());
            }
        });

    }


    public void testPubSub() throws DocumentException, InterruptedException {

        Thread.sleep(100);

        preparation.proceedInitiate(getFakeInitiateIQ("0.0.0.0"),
                new CallSession("1010", new JID("test@jabber.org")));

        preparation.proceedTerminate(getFakeTerminateIQ(),
                new CallSession("1010", new JID("test@jabber.org")));

        String message = queue.poll(10, TimeUnit.SECONDS);

        assertTrue(message.contains("\"action\": \"session-initiate\","));

        message = queue.poll(10, TimeUnit.SECONDS);

        assertTrue(message.contains("\"action\": \"session-terminate\","));

    }

    static class MyListener extends JedisPubSub {
        public void onMessage(String channel, String message) {
            System.out.println("Message ["+channel+"] = "+message);
            queue.offer(message);
        }

        public void onSubscribe(String channel, int subscribedChannels) {
        }

        public void onUnsubscribe(String channel, int subscribedChannels) {
        }

        public void onPSubscribe(String pattern, int subscribedChannels) {
        }

        public void onPUnsubscribe(String pattern, int subscribedChannels) {

        }

        public void onPMessage(String pattern, String channel,
                               String message) {
            System.out.println("PMessage ["+channel+"] = "+message);
        }
    }

    private JingleIQ getFakeInitiateIQ(String ip) throws DocumentException {
        final String initiator = "+553496788900@localhost.com";
        final String responder = "+553496788901@localhost.com";
        final String packet = "<jingle xmlns=\"urn:xmpp:jingle:1\" action=\"session-initiate\" initiator=\"" +
                initiator + "\" responder=\"" + responder + "\" sid=\"37665\"><content xmlns=\"\" creator=\"" +
                "initiator\" name=\"audio\" senders=\"both\"><description xmlns=\"urn:xmpp:jingle:apps:rtp:1\">" +
                "<payload-type xmlns=\"\" id=\"0\" name=\"PCMU\"/></description><transport xmlns=\"" +
                "urn:xmpp:jingle:transports:raw-udp:1\"><candidate xmlns=\"\" ip=\""+ip+"\" port=\"22000\" " +
                "generation=\"0\"/></transport></content></jingle>";
        Document doc = DocumentHelper.parseText(packet);
        final IQ iq = new IQ(doc.getRootElement());
        iq.setFrom("romeo@localhost");
        iq.setTo("juliet@localhost");
        final JingleIQ jingleIQ = JingleIQ.fromXml(iq);
        return JingleIQ.fromXml(jingleIQ);
    }

    public JingleIQ getFakeTerminateIQ() {
        final String initiator = "+553496788900@localhost.com";
        final String responder = "+553496788901@localhost.com";
        final Jingle jingle = new Jingle("abc", initiator, responder, Jingle.SESSION_TERMINATE);
        jingle.setReason(new Reason("Hello", Reason.Type.incompatible_parameters));
        final JingleIQ jingleIQ = new JingleIQ(jingle);
        jingleIQ.setFrom("x@b.c");
        jingleIQ.setTo("y@b.c");
        return jingleIQ;
    }
}
