package org.jinglenodes.session.persistence;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.log4j.Logger;
import org.jinglenodes.jingle.processor.JingleException;
import org.jinglenodes.session.CallSession;
import org.jinglenodes.session.DefaultCallSessionMapper;
import org.jinglenodes.session.SessionUpdateListener;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 5/24/12
 * Time: 8:36 PM
 */
public class PersistentCallSessionMapper extends DefaultCallSessionMapper implements SessionUpdateListener {

    final private static Logger log = Logger.getLogger(PersistentCallSessionMapper.class);
    final private XStream xStream;
    final private String ENCODE = "UTF-8";
    private PersistenceWriterQueue persistenceWriterQueue;
    private PersistenceWriter writer;

    public PersistentCallSessionMapper() {
        super();
        xStream = new XStream(new DomDriver());
        xStream.processAnnotations(CallSession.class);

        //Custom Omit Fields
        xStream.omitField(Message.class, "sendTo");
        xStream.omitField(Message.class, "arrivedAt");
        xStream.omitField(Message.class, "participants");

    }

    public void init() {
        persistenceWriterQueue = new PersistenceWriterQueue(writer);
        load();
    }

    private void load() {

        final List<byte[]> data = writer.loadData();

        for (final byte[] entry : data) {

            try {
                final CallSession cs = fromXml(unzip(entry));
                log.debug("Loaded CallSession: " + cs.getId());
                sessionMap.put(cs.getId(), cs);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @Override
    protected CallSession createSession(Message message) throws JingleException {
        final CallSession session = super.createSession(message);
        session.setSessionUpdateListener(this);
        return session;
    }

    @Override
    protected CallSession createSession(JingleIQ jingle) {
        final CallSession session = super.createSession(jingle);
        session.setSessionUpdateListener(this);
        return session;
    }

    public CallSession fromXml(final String xml) throws Exception {
        return (CallSession) xStream.fromXML(xml);
    }

    public String toXml(final CallSession session) throws Exception {
        return xStream.toXML(session);
    }

    public byte[] zip(final String data) throws UnsupportedEncodingException, IOException {
        byte[] input = data.getBytes(ENCODE);
        Deflater df = new Deflater();
        df.setInput(input);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(input.length);
        df.finish();
        byte[] buff = new byte[1024];
        while (!df.finished()) {
            int count = df.deflate(buff);
            baos.write(buff, 0, count);
        }
        baos.close();
        byte[] output = baos.toByteArray();

        System.out.println("Original: " + input.length);
        System.out.println("Compressed: " + output.length);
        return output;
    }

    public String unzip(final String data) throws IOException, DataFormatException {
        return unzip(data.getBytes(ENCODE));
    }

    public String unzip(byte[] input) throws IOException, DataFormatException {
        Inflater ifl = new Inflater();
        ifl.setInput(input);

        ByteArrayOutputStream baos = new ByteArrayOutputStream(input.length);
        byte[] buff = new byte[1024];
        while (!ifl.finished()) {
            int count = ifl.inflate(buff);
            baos.write(buff, 0, count);
        }
        baos.close();
        byte[] output = baos.toByteArray();

        return new String(output);
    }

    @Override
    public void sessionUpdated(CallSession session) {
        if (persistenceWriterQueue != null) {
            try {
                persistenceWriterQueue.persist(session.getId(), zip(toXml(session)));
            } catch (Exception e) {
                log.error("Could Not Persist CallSession", e);
            }
        }
    }

    @Override
    public void sessionDestroyed(CallSession session) {
        if (persistenceWriterQueue != null) {
            try {
                persistenceWriterQueue.delete(session.getId());
            } catch (Exception e) {
                log.error("Could Not Persist CallSession", e);
            }
        }
    }

    public void setWriter(final PersistenceWriter writer) {
        this.writer = writer;
    }
}
