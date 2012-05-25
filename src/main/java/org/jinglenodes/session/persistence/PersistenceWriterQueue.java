package org.jinglenodes.session.persistence;

import java.util.concurrent.*;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 5/24/12
 * Time: 11:29 PM
 */
public class PersistenceWriterQueue {

    private final BlockingQueue<Runnable> linkedBlockingDeque = new LinkedBlockingDeque<Runnable>(50);
    private final ExecutorService executorService = new ThreadPoolExecutor(1, 1, 15, TimeUnit.SECONDS, linkedBlockingDeque, new ThreadPoolExecutor.CallerRunsPolicy());
    final private PersistenceWriter writer;

    public PersistenceWriterQueue(PersistenceWriter writer) {
        this.writer = writer;
    }

    public void persist(final String id, byte[] data) {
        executorService.submit(new PersistenceTask(id, data, writer));
    }

    public void delete(final String id) {
        executorService.submit(new PersistenceTask(id, writer));
    }
}
