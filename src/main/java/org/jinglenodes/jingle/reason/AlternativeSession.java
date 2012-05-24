package org.jinglenodes.jingle.reason;

import org.dom4j.Element;

/**
 * Created with IntelliJ IDEA.
 * User: pepe
 * Date: 24/05/12
 * Time: 12:05
 * To change this template use File | Settings | File Templates.
 */
public class AlternativeSession extends ReasonType {
    private final static String SID = "sid";

    public AlternativeSession(Name type, String sid) {
        super(type);
        Element se = this.addElement(SID);
        se.addCDATA(sid);
    }

    public String getSid() {
        return this.elementText(SID);
    }

    public static ReasonType fromElement(Element element) {
        if (element instanceof AlternativeSession)
            return (AlternativeSession) element;

        Name name;
        try {
            name = Name.valueOf(element.getName().replace('-', '_'));
        } catch (Exception e) {
            return null;
        }

        if (name.equals(Name.alternative_session)) {
            return new AlternativeSession(Name.alternative_session, element.elementText(SID));
        }
        return null;
    }
}
