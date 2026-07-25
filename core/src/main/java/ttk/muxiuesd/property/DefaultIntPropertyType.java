package ttk.muxiuesd.property;

import game.muxiuesd.bedrockcore.app.interfaces.data.DataReader;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataWriter;
import game.muxiuesd.bedrockcore.serialization.Codec;

public class DefaultIntPropertyType extends PropertyType<Integer>{

    @Override
    public void write (DataWriter<?> writer, Integer data) {
        writer.writeInt(getId(), data);
    }

    @Override
    public Integer read (DataReader<?> reader, String dataKey) {
        return reader.readInt(dataKey);
    }

    @Override
    public Codec<Integer> getValueCodec () {
        return Codec.INT;
    }
}
