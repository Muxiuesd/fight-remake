package ttk.muxiuesd.property;

import game.muxiuesd.bedrockcore.app.interfaces.data.DataReader;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataWriter;

public class DefaultStringPropertyType extends PropertyType<String> {
    @Override
    public void write (DataWriter<?> writer, String data) {
        writer.writeString(getId(), data);
    }

    @Override
    public String read (DataReader<?> reader, String dataKey) {
        return reader.readString(dataKey);
    }
}
