/**
 *
 */
package org.jinglenodes.stats;

import org.jinglenodes.component.SIPGatewayApplication;

/**
 * @author bhlangonijr
 */
public class SwitjiStatsImpl implements SwitjiStats {

    private SIPGatewayApplication sipApplication;

    /* (non-Javadoc)
      * @see org.jinglenodes.stats.SwitjiStats#getOpenSessions()
      */
    @Override
    public int getOpenSessions() {
        return sipApplication.getJingleProcessor().getCallSessionMapper().getSessionCount();
    }

    public SIPGatewayApplication getSipApplication() {
        return sipApplication;
    }

    public void setSipApplication(SIPGatewayApplication sipApplication) {
        this.sipApplication = sipApplication;
    }

}
