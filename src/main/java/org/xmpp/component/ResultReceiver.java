package org.xmpp.component;

/**
 * Created by IntelliJ IDEA.
 * User: Thiago
 * Date: 09/02/12
 * Time: 12:01
 */

public interface ResultReceiver {

    public void receivedResult(IqRequest IqRequest);

    public void receivedError(IqRequest IqRequest);

    public void timeoutRequest(IqRequest IqRequest);

}
