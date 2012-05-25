package org.jinglenodes.session.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 5/24/12
 * Time: 11:31 PM
 */
public class PersistenceTask implements Runnable {

    final private String id;
    final PersistenceWriter writer;
    private byte[] data;

    public PersistenceTask(final String id, final byte[] data, final PersistenceWriter writer) {
        this.id = id;
        this.data = data;
        this.writer = writer;
    }

    public PersistenceTask(final String id, final PersistenceWriter writer) {
        this.id = id;
        this.writer = writer;
    }

    @Override
    public void run() {
        if (data != null) {
            writer.write(id, data);
        } else {
            writer.delete(id);
        }
    }
}
