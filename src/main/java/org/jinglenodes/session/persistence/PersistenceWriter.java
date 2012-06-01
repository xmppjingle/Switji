package org.jinglenodes.session.persistence;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 5/24/12
 * Time: 11:24 PM
 */
public interface PersistenceWriter {

    public void write(final String id, final byte[] data);

    public void delete(final String id);

    public List<byte[]> loadData();

    public void reset();

}
