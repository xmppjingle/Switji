package org.jinglenodes.prepare;

import org.xmpp.packet.IQ;

/**
 * Service Locator is meant to select/resolve the service address which
 * will be used by a processor
 * @see org.xmpp.component.AbstractServiceProcessor
 *
 * @author bhlangonijr
 *         Date: 3/3/14
 *         Time: 11:33 AM
 */
public interface ServiceLocator {

    /**
     * Get service URI based on IQ data
     * @param request
     * @return
     */
    String getServiceUri(IQ request);

}
