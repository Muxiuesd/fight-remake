package ttk.muxiuesd.property;

import game.muxiuesd.bedrockcore.app.interfaces.data.DataReader;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataWriter;
import game.muxiuesd.bedrockcore.serialization.Codec;

/**
 * 默认的字符串属性类型
 * */
public class DefaultStringPropertyType extends PropertyType<String> {
    @Override
    public void write (DataWriter<?> writer, String data) {
        writer.writeString(getId(), data);
    }

    @Override
    public String read (DataReader<?> reader, String dataKey) {
        return reader.readString(dataKey);
    }

    @Override
    public Codec<String> getValueCodec () {
        return Codec.STRING;
    }
}
