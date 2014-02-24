package org.xmpp.tinder.parser;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.reflection.FieldDictionary;
import com.thoughtworks.xstream.converters.reflection.SortableFieldKeySorter;
import com.thoughtworks.xstream.converters.reflection.SunUnsafeReflectionProvider;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.mapper.MapperWrapper;
import org.dom4j.Element;
import org.jinglenodes.jingle.Jingle;
import org.jinglenodes.jingle.Reason;
import org.jinglenodes.util.ReasonConverter;
import org.xmpp.packet.IQ;

public class XStreamIQ<T> extends IQ {

    static SortableFieldKeySorter sorter = new SortableFieldKeySorter();

    static {
        sorter.registerFieldOrder(Reason.class, new String[] {"type", "REASON", "CONDITION", "text"});
    };

    final static XStream stream = new XStream(new SunUnsafeReflectionProvider(new FieldDictionary(sorter)), new DomDriver()) {
        protected MapperWrapper wrapMapper(MapperWrapper next) {
            return new MapperWrapper(next) {
                public boolean shouldSerializeMember(Class definedIn, String fieldName) {
                    return definedIn != Object.class && super.shouldSerializeMember(definedIn, fieldName);
                }
            };
        }
    };

    static {
        stream.registerConverter(new ReasonConverter());
        stream.autodetectAnnotations(true);
        com.thoughtworks.xstream.annotations.Annotations.configureAliases(stream, Jingle.class);

    }

    public static XStream getStream() {
        return stream;
    }

    public XStreamIQ() {
    }

    public void setElement(final Element element) {
        this.element = element;
    }

    @Override
    public Element getChildElement() {
        return element;
    }

    public String getChildElementXML() {
        return this.element.toString();
    }
}
