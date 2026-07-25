package ttk.muxiuesd.property;

import game.muxiuesd.bedrockcore.app.interfaces.data.DataReader;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataWriter;
import game.muxiuesd.bedrockcore.serialization.Codec;
import ttk.muxiuesd.registrant.Registries;

/**
 * 默认的字符串属性类型
 * */
public class DefaultStringPropertyType extends PropertyType<String> {
    public static final Codec<PropertyType<String>> CODEC = Codec.STRING.xmap(
        (id) -> (PropertyType<String>) Registries.PROPERTY_TYPE.get(id),
        PropertyType::getId
    );

    @Override
    public void write (DataWriter<?> writer, String data) {
        writer.writeString(getId(), data);
    }

    @Override
    public String read (DataReader<?> reader, String dataKey) {
        return reader.readString(dataKey);
    }

    @Override
    public Codec<PropertyType<String>> getCodec () {
        return CODEC;
    }

    @Override
    public Codec<String> getValueCodec () {
        return Codec.STRING;
    }
}
