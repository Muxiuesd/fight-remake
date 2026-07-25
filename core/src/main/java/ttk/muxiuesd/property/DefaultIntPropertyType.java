package ttk.muxiuesd.property;

import game.muxiuesd.bedrockcore.app.interfaces.data.DataReader;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataWriter;
import game.muxiuesd.bedrockcore.serialization.Codec;
import ttk.muxiuesd.registrant.Registries;

public class DefaultIntPropertyType extends PropertyType<Integer>{

    public static final Codec<PropertyType<Integer>> CODEC = Codec.STRING.xmap(
        (id) -> (PropertyType<Integer>) Registries.PROPERTY_TYPE.get(id),
        PropertyType::getId
    );

    @Override
    public void write (DataWriter<?> writer, Integer data) {
        writer.writeInt(getId(), data);
    }

    @Override
    public Integer read (DataReader<?> reader, String dataKey) {
        return reader.readInt(dataKey);
    }



    @Override
    public Codec<PropertyType<Integer>> getCodec () {
        return CODEC;
    }

    @Override
    public Codec<Integer> getValueCodec () {
        return Codec.INT;
    }
}
