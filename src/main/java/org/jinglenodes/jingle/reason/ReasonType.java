package org.jinglenodes.jingle.reason;

import org.dom4j.Element;
import org.dom4j.tree.BaseElement;

/**
 * Created with IntelliJ IDEA.
 * User: pepe
 * Date: 24/05/12
 * Time: 11:59
 * Class to create reason types
 */
public class ReasonType extends BaseElement {

    public enum Name {
        alternative_session,
        busy,
        cancel,
        connectivity_error,
        decline,
        expired,
        failed_application,
        failed_transport,
        general_error,
        gone,
        incompatible_parameters,
        media_error,
        security_error,
        success,
        timeout,
        unsupported_applications,
        unsupported_transports,
        payment;

        public String toString() {
            return this.name().replace('_', '-');
        }
    }

    public ReasonType(Name name) {
        super(name.toString());
    }

    public Name getNameType() {
        Name name;
        try {
            name = Name.valueOf(this.getName().replace('-', '_'));
        } catch (Exception e) {
            return null;
        }
        return name;
    }

    public static ReasonType fromElement(Element element) {
        if (element instanceof ReasonType)
            return (ReasonType) element;

        String elementName = element.getName();
        if (elementName.equals(Name.alternative_session.toString())) {
            return AlternativeSession.fromElement(element);
        } else {
            Name name;
            try {
                name = Name.valueOf(elementName.replace('-', '_'));
            } catch (Exception e) {
                return null;
            }
            return new ReasonType(name);
        }
    }

}
