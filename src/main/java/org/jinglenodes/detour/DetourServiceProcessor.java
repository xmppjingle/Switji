package org.jinglenodes.detour;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.QName;
import org.jinglenodes.sip.account.CachedSipAccountProvider;
import org.jinglenodes.sip.account.SipAccount;
import org.xmpp.component.AbstractServiceProcessor;
import org.xmpp.component.IqRequest;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.tinder.JingleIQ;
import org.zoolu.sip.message.JIDFactory;
import org.zoolu.sip.provider.SipProviderInformation;

/**
 * Created by IntelliJ IDEA.
 * User: thiago
 * Date: 2/21/12
 * Time: 5:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class DetourServiceProcessor extends AbstractServiceProcessor {
    private final Logger log = Logger.getLogger(DetourServiceProcessor.class);
    private CachedSipAccountProvider accountProvider;
    private SipProviderInformation sipInfo;
    private String phoneDefaultType;

    private final Element requestElement;
    private final String xmlns;
    private String accountService;

    public DetourServiceProcessor(final String elementName, final String xmlns) {
        this.xmlns = xmlns;
        this.requestElement = DocumentHelper.createElement(new QName(elementName, new Namespace("", xmlns)));
    }

    @Override
    public IQ createServiceRequest(Object object, String fromNode, String toNode) {
        final IQ request = new IQ(IQ.Type.get);
        final JID toService = JIDFactory.getInstance().getJID(toNode + "@" + accountService);
        request.setTo(toService);
        request.setChildElement(requestElement.createCopy());
        if (log.isDebugEnabled()) {
            log.debug("createServiceRequest: " + request.toXML());
        }
        return request;
    }

    @Override
    protected String getRequestId(Object obj) {
        if (obj instanceof JingleIQ) {
            final JingleIQ iq = (JingleIQ) obj;
            return iq.getJingle().getSid();
        }
        return null;
    }

    @Override
    protected void handleResult(IqRequest iq) {
        if (iq.getOriginalPacket() instanceof IQ) {
            final IQ originalIq = (IQ) iq.getOriginalPacket();
            final JID from = originalIq.getFrom();
            final SipAccount sipAccount = getSipAccount(iq.getResult());
            if (sipAccount != null) {
                accountProvider.addSipAccount(from, sipAccount);
            } else {
                log.error("SEVERE Empty SIP Account Retrieved.");
            }
        }
    }

    /*
      * Create the sip account based on
      * the information retrieved from the iq
      */
    protected SipAccount getSipAccount(IQ iq) {
        String phone = null;
        for (Object o : iq.getChildElement().elements()) {
            Element e = (Element) o;
            if (e.attributeValue("type").equals(phoneDefaultType)) {
                phone = e.attributeValue("number");
                break;
            } else if (phone == null) {
                phone = e.attributeValue("number");
            }
        }

        return phone != null ? new SipAccount(phone, phone, phone, "", sipInfo.getIP(), sipInfo.getViaAddress()) : null;
    }

    @Override
    protected void handleError(IQ iq) {
        log.error("Failed to Retrieve Account: " + iq.toXML());
    }

    @Override
    protected void handleTimeout(IqRequest request) {

    }

    @Override
    public String getNamespace() {
        return xmlns;
    }

    public String getAccountService() {
        return accountService;
    }

    public void setAccountService(String accountService) {
        this.accountService = accountService;
    }

    public CachedSipAccountProvider getAccountProvider() {
        return accountProvider;
    }

    public void setAccountProvider(CachedSipAccountProvider accountProvider) {
        this.accountProvider = accountProvider;
    }

    /**
     * @return the sipInfo
     */
    public SipProviderInformation getSipInfo() {
        return sipInfo;
    }

    /**
     * @param sipInfo the sipInfo to set
     */
    public void setSipInfo(SipProviderInformation sipInfo) {
        this.sipInfo = sipInfo;
    }

    /**
     * @return the phoneDefaultType
     */
    public String getPhoneDefaultType() {
        return phoneDefaultType;
    }

    /**
     * @param phoneDefaultType the phoneDefaultType to set
     */
    public void setPhoneDefaultType(String phoneDefaultType) {
        this.phoneDefaultType = phoneDefaultType;
    }
}
