package org.jinglenodes.util;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.jinglenodes.jingle.Reason;

/**
 * @author bhlangonijr
 *         Date: 2/23/14
 *         Time: 2:07 PM
 */
public class ReasonConverter implements Converter {
    @Override
    public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
        Reason reason = (Reason) value;
        writer.startNode(reason.getType().toString().replace("_", "-"));
        writer.endNode();
        if (reason.getText() != null) {
            writer.startNode("text");
            writer.setValue(reason.getText());
            writer.endNode();
        }

    }

    @Override
    public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
        Reason reason = new Reason();
        reader.moveDown();
        reason.setType(Reason.Type.valueOf(reader.getNodeName().replace("-", "_")));
        reader.moveUp();
        if (reader.hasMoreChildren()) {
            reader.moveDown();
            reason.setText(reader.getValue());
            reader.moveUp();
        }
        return reason;
    }

    @Override
    public boolean canConvert(Class aClass) {
        return aClass.equals(Reason.class);
    }
}
