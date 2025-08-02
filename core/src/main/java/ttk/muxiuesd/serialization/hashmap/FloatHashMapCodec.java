package ttk.muxiuesd.serialization.hashmap;

import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
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
