package org.jinglenodes.stats;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 5/24/12
 * Time: 2:42 PM
 */
public interface SwitjiAdmin {

    public boolean killSession(String sessionId);

    public int killAll();

}
