package ttk.muxiuesd.property;

import game.muxiuesd.bedrockcore.app.interfaces.data.DataReader;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataWriter;

public class DefaultFloatPropertyType extends PropertyType<Float> {
    @Override
    public void write (DataWriter<?> writer, Float data) {
        writer.writeFloat(getId(), data);
    }

    @Override
    public Float read (DataReader<?> reader, String dataKey) {
        return reader.readFloat(getId());
    }
}
