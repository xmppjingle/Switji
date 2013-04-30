package org.xmpp.component;

import org.apache.log4j.Logger;
import org.jinglenodes.util.ConcurrentExpirableHashMap;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.packet.PacketError;
import org.xmpp.packet.PacketError.Condition;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Thiago
 * Date: 09/02/12
 * Time: 12:01
 */
public abstract class AbstractServiceProcessor implements NamespaceProcessor {
    static final Logger log = Logger.getLogger(AbstractServiceProcessor.class);
    protected ExternalComponent component;

    private int maxTries = 3;
    private long timeout = 15000;
    private int timeoutInterval = 100;
    private int requestCounter = 0;

    private final ConcurrentExpirableHashMap<String, IqRequest> pendingService =
            new ConcurrentExpirableHashMap<String, IqRequest>();

    private final ConcurrentExpirableHashMap<String, IqRequest> pendingServiceResult =
            new ConcurrentExpirableHashMap<String, IqRequest>();

    public void init() {
        if (component != null) {
            component.addProcessor(this);
        }

        pendingService.setTtl(timeout);
        pendingServiceResult.setTtl(timeout);

        pendingService.disableScheduledPurge();
        pendingServiceResult.disableScheduledPurge();

        pendingService.setPurgeCounterLimit(0);
        pendingServiceResult.setPurgeCounterLimit(0);

    }

    public abstract IQ createServiceRequest(final Object object, final String fromNode, final String toNode);

    public void queryService(Object object, String from, String to, final ResultReceiver resultReceiver) throws ServiceException {
        IqRequest iqRequest = pendingService.get(getRequestId(object));
        if (iqRequest == null) {
            iqRequest = createIqRequest(object, from, to, resultReceiver);
        }

        queryService(iqRequest);
    }

    public void queryService(final IqRequest iqRequest) throws ServiceException {
        if (iqRequest.getTries() < getMaxTries()) {
            iqRequest.incTries();
            log.debug("Querying Service: " + iqRequest.getRequest().toXML());
            component.send(iqRequest.getRequest());
        } else {
            final String msg = iqRequest.getOriginalPacket().getClass().getCanonicalName() + " ID: " + getRequestId(iqRequest.getOriginalPacket());
            log.warn("Retries exceeded for: " + msg);
            deleteIqRequest(iqRequest.getOriginalPacket());
            throw new ServiceException("Tries Exceeded for " + msg);
        }
        if (requestCounter++ > timeoutInterval) {
            requestCounter = 0;
            _checkTimeout();
        }
    }

    private void _checkTimeout() {
        final List<IqRequest> timeoutRequests = pendingServiceResult.cleanUpExpired(timeout);
        pendingService.cleanUpExpired(timeout);
        for (final IqRequest iqRequest : timeoutRequests) {
            if (iqRequest != null) {
                log.debug("Request Timeout: " + iqRequest.getRequest().toXML());
                handleTimeout(iqRequest);
                ResultReceiver rr = iqRequest.getResultReceiver();
                if (rr != null) {
                    rr.timeoutRequest(iqRequest);
                }
            } else {
                log.warn("null IqRequest Expired");
            }
        }
    }

    private IqRequest deleteIqRequest(IQ iq) {
        final IqRequest iqRequest = pendingServiceResult.remove(iq.getID());
        if (iqRequest != null) {
            pendingService.remove(getRequestId(iqRequest.getOriginalPacket()));
        }
        return iqRequest;
    }

    private IqRequest deleteIqRequest(Object object) {
        final IqRequest iqRequest = pendingService.remove(getRequestId(object));
        if (iqRequest != null) {
            pendingServiceResult.remove(iqRequest.getRequest().getID());
        }
        return iqRequest;
    }

    private IqRequest createIqRequest(Object object, String fromNode, String toNode, ResultReceiver resultReceiver) throws ServiceException {
        IqRequest iqRequest;
        IQ serviceRequest = createServiceRequest(object, fromNode, toNode);

        if (serviceRequest == null) {
            throw new ServiceException("Could NOT Create Request for: " + object + " From:" + fromNode + " To:" + toNode);
        }

        iqRequest = new IqRequest(object, serviceRequest, resultReceiver);
        pendingServiceResult.put(serviceRequest.getID(), iqRequest);
        pendingService.put(getRequestId(object), iqRequest);
        log.debug("Added Pending IQ: " + iqRequest.getRequest().getID() + "\n" + iqRequest.getRequest().toXML());
        return iqRequest;
    }

    protected abstract String getRequestId(Object obj);

    protected abstract void handleResult(IqRequest iq);

    protected abstract void handleError(IqRequest iq);

    protected abstract void handleTimeout(IqRequest request);

    @Override
    public IQ processIQGet(IQ iq) {
        return _createPacketError(iq, Condition.bad_request);
    }

    @Override
    public IQ processIQSet(IQ iq) {
        return _createPacketError(iq, Condition.bad_request);
    }

    @Override
    public void processIQError(IQ iq) {
        final IqRequest iqRequest = deleteIqRequest(iq);
        if (iqRequest != null) {
            log.debug("processError: " + iq.toXML());
            iqRequest.setResult(iq);
            handleError(iqRequest);
            iqRequest.getResultReceiver().receivedError(iqRequest);
        }
    }

    @Override
    public void processIQResult(IQ iq) {
        log.debug("result Received: " + iq.toXML());
        final IqRequest iqRequest = deleteIqRequest(iq);
        if (iqRequest != null) {
            iqRequest.setResult(iq);
            log.debug("processResult: " + iq.toXML());
            handleResult(iqRequest);
            iqRequest.getResultReceiver().receivedResult(iqRequest);
        }
    }

    public ExternalComponent getComponent() {
        return component;
    }

    public void setComponent(ExternalComponent component) {
        this.component = component;
    }

    protected IQ _createPacketError(final IQ iq, final Condition condition) {
        final PacketError pe = new PacketError(condition);
        final IQ ret = IQ.createResultIQ(iq);
        ret.setError(pe);
        return ret;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public int getMaxTries() {
        return maxTries;
    }

    public void setMaxTries(int max_tries) {
        this.maxTries = max_tries;
    }

    public int getTimeoutInterval() {
        return timeoutInterval;
    }

    public void setTimeoutInterval(int timeoutInterval) {
        this.timeoutInterval = timeoutInterval;
    }

    public JID getComponentJID() {
        return component.getJID();
    }
}
