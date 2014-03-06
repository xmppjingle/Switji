package org.jinglenodes.relay;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.maxmind.db.Reader;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import org.apache.log4j.Logger;
import org.jinglenodes.jingle.transport.Candidate;
import org.jinglenodes.prepare.NodeFormat;
import org.jinglenodes.prepare.PrefixNodeFormat;
import org.jinglenodes.prepare.ServiceLocator;
import org.jinglenodes.util.Util;
import org.xmpp.packet.IQ;
import org.xmpp.packet.JID;
import org.xmpp.tinder.JingleIQ;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Map;

/**
 * @author bhlangonijr
 *         Date: 3/3/14
 *         Time: 2:53 PM
 */
public class RelayServiceLocator implements ServiceLocator {

    final Logger log = Logger.getLogger(RelayServiceLocator.class);

    private final String geoDbFile;
    private final PhoneNumberUtil phoneUtil;
    private Map<String, String> relayServices;
    private Map<String, String> relayServicesByCode;
    private NodeFormat nodeFormat;
    private static final String classpath = "classpath:";

    private final DatabaseReader reader;

    public RelayServiceLocator(final String geoDbFile) throws IOException {
        String path;

        if (geoDbFile.startsWith("classpath")) {
            path = getClass().getResource(geoDbFile.split(classpath)[1]).getPath();
        } else {
            path = new URL("file://"+geoDbFile).getPath();
        }

        this.geoDbFile = geoDbFile;
        reader =  new DatabaseReader.Builder(new File(path)).fileMode(Reader.FileMode.MEMORY).build();
        phoneUtil = PhoneNumberUtil.getInstance();
        nodeFormat = new PrefixNodeFormat();
    }

    /**
     * Get the closest relay based on IP distance between caller (From IP) and relay,
     * In case it is not possible to find it by IP, country code from caller will be
     * used as a last resort
     *
     * Relay must be distributed geographically
     *
     * @param request
     * @return
     */
    @Override
    public String getServiceUri(IQ request) {

        String uri = null;
        try {
            if (request != null && request instanceof JingleIQ) {

                final JingleIQ jingleIQ = JingleIQ.fromXml(request);

                try {
                    uri = findByIP(jingleIQ);
                } catch (Exception e1) {
                    log.error("Couldn't find closest relay by IP", e1);
                }
                // last resort - try country code from caller number
                if (uri == null) {
                    try {
                        uri = findByCountryCode(jingleIQ);
                    } catch (Exception e2) {
                        log.error("Couldn't find closest relay by country code", e2);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error retrieving relay service", e);
        }

        return uri;
    }

    /*
     * Find best relay based on IP
     *
     * @param jingleIQ
     * @return
     * @throws IOException
     * @throws GeoIp2Exception
     */
    private String findByIP(JingleIQ jingleIQ) throws IOException, GeoIp2Exception {

        InetAddress ip = null;
        String uri = null;
        if (getRelayServices() != null) {
            for (Candidate c : jingleIQ.getJingle().getContent().getTransport().getCandidates()) {
                try {
                    ip = InetAddress.getByName(c.getIp());
                    if (log.isDebugEnabled()) {
                        log.debug("IP found: "+ip.getHostAddress());
                    }
                    if (ip != null) {
                        CityResponse cityResponse = reader.city(ip);
                        if (cityResponse != null) {
                            final String city = cityResponse.getCity().getName();
                            final String country = cityResponse.getCountry().getName();

                            log.debug("City found: " + city + ", " +
                                    "Country: " + country);

                            if (city != null && !city.equals(""))  {
                                uri = getRelayServices().get(city);
                                log.debug("City [" + city + "]=" + uri);
                                break;
                            } else if (country != null && !country.equals("")) {
                                uri = getRelayServices().get(country);
                                log.debug("Country ["+country+"]=" + uri);
                                break;
                            } else {
                                log.debug("No city or country found for: " + ip.getHostAddress());
                            }

                        } else {
                            log.debug("cityResponse == null for " + ip.getHostAddress());
                        }
                    }
                } catch (UnknownHostException ue) {
                    //do nothing
                }
            }
        } else {
            log.warn("There is no map defined for looking up City or Country relays");
        }
        return uri;
    }

    private String findByCountryCode(JingleIQ jingleIQ) throws NumberParseException {
        String uri = null;
        if (getRelayServicesByCode() != null) {
            final JID jid = new JID(jingleIQ.getJingle().getInitiator());
            final String phone = nodeFormat.formatNode(jid.getNode(), "");
            if (Util.isNumeric(phone)) {
                Phonenumber.PhoneNumber numberProto = phoneUtil.parse(phone, "");
                int code = numberProto.getCountryCode();
                uri = getRelayServicesByCode().get("+"+code);
            }
        } else {
            log.warn("There is no map defined for looking up relays by country phone code");
        }
        return uri;
    }

    public String getGeoDbFile() {
        return geoDbFile;
    }

    public Map<String, String> getRelayServices() {
        return relayServices;
    }

    public void setRelayServices(Map<String, String> relayServices) {
        this.relayServices = Collections.unmodifiableMap(relayServices);
    }

    public Map<String, String> getRelayServicesByCode() {
        return relayServicesByCode;
    }

    public void setRelayServicesByCode(Map<String, String> relayServicesByCode) {
        this.relayServicesByCode = Collections.unmodifiableMap(relayServicesByCode);
    }

    public NodeFormat getNodeFormat() {
        return nodeFormat;
    }

    public void setNodeFormat(NodeFormat nodeFormat) {
        this.nodeFormat = nodeFormat;
    }
}
