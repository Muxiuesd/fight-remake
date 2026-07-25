package ttk.muxiuesd.serialization.hashmap;

import game.muxiuesd.bedrockcore.data.JsonDataReader;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import ttk.muxiuesd.serialization.abs.WorldInfoHashMap;

public class FloatHashMapCodec extends WorldInfoHashMap<FloatHashMapCodec, Float> {
    @Override
    public void encode (JsonDataWriter writer) {
        forEach(writer::writeFloat);
    }

    @Override
    public void decode (JsonDataReader reader) {
        reader.getParse().forEach(pair -> {
            //这么写才不会出错
            put(pair.name(), pair.asFloat());
        });
    }
}
