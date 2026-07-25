package ttk.muxiuesd.property;

import game.muxiuesd.bedrockcore.app.interfaces.data.DataReader;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataWriter;
import game.muxiuesd.bedrockcore.serialization.Codec;
import ttk.muxiuesd.registrant.Registries;

public class DefaultFloatPropertyType extends PropertyType<Float> {
    public static final Codec<PropertyType<Float>> CODEC = Codec.STRING.xmap(
        (id) -> (PropertyType<Float>) Registries.PROPERTY_TYPE.get(id),
        PropertyType::getId    // 编码：PropertyType<?> → id 字符串
    );

    @Override
    public void write (DataWriter<?> writer, Float data) {
        writer.writeFloat(getId(), data);
    }

    @Override
    public Float read (DataReader<?> reader, String dataKey) {
        return reader.readFloat(getId());
    }

    @Override
    public Codec<PropertyType<Float>> getCodec () {
        return CODEC;
    }

    @Override
    public Codec<Float> getValueCodec () {
        return Codec.FLOAT;
    }
}
