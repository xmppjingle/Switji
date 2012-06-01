package org.jinglenodes.session.persistence;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.log4j.Logger;
import org.jinglenodes.jingle.processor.JingleException;
import org.jinglenodes.prepare.CallPreparation;
import org.jinglenodes.session.CallSession;
import org.jinglenodes.session.DefaultCallSessionMapper;
import org.jinglenodes.session.SessionUpdateListener;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
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
    private boolean compressed = true;
    private List<CallPreparation> preparations = new ArrayList<CallPreparation>();

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

    public void load() {

        final List<byte[]> data = writer.loadData();

        if (data == null) {
            log.warn("No Session Data to Restore!");
            return;
        }

        for (final byte[] entry : data) {

            try {
                final CallSession cs = fromXml(isCompressed() ? unzip(entry) : new String(entry, ENCODE));

                if (cs != null) {
                    log.debug("Loaded CallSession: " + cs.getId());

                    if (cs.getPreparations() == null) {
                        cs.setPreparations(new ConcurrentLinkedQueue<CallPreparation>());
                    }
                    if (cs.getProceeds() == null) {
                        cs.setProceeds(new ConcurrentLinkedQueue<CallPreparation>());
                        cs.getProceeds().addAll(preparations.subList(0, preparations.size()));
                    }

                    cs.setSessionUpdateListener(this);

                    sessionMap.put(cs.getId(), cs);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public void reset() {
        writer.reset();
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

        log.debug("Original: " + input.length + "b - Compressed: " + output.length + "b");
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
                final String s = toXml(session);
                persistenceWriterQueue.persist(getPersistentId(session), isCompressed() ? zip(s) : s.getBytes(ENCODE));
            } catch (Exception e) {
                log.error("Could Not Persist CallSession", e);
            }
        }
    }

    @Override
    public void sessionDestroyed(CallSession session) {
        if (persistenceWriterQueue != null) {
            try {
                persistenceWriterQueue.delete(getPersistentId(session));
            } catch (Exception e) {
                log.error("Could Not Persist CallSession", e);
            }
        }
    }

    private String getPersistentId(final CallSession cs) {
        return "CS:" + cs.getId();
    }

    public void setWriter(final PersistenceWriter writer) {
        this.writer = writer;
    }

    public void setPreparations(List<CallPreparation> preparations) {
        this.preparations = preparations;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }
}
