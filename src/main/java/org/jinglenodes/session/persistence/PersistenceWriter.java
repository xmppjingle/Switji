package org.jinglenodes.session.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 5/24/12
 * Time: 11:24 PM
 */
public interface PersistenceWriter {

    public void write(final String id, final byte[] data);

    public void delete(final String id);

}
