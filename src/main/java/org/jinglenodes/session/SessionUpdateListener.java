package org.jinglenodes.session;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 5/24/12
 * Time: 11:42 PM
 */
public interface SessionUpdateListener {
    public void sessionUpdated(final CallSession session);

    public void sessionDestroyed(final CallSession session);
}
