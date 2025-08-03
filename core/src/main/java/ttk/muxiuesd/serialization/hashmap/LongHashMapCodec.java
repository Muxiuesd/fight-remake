package ttk.muxiuesd.serialization.hashmap;

import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.serialization.abs.WorldInfoHashMap;

public class LongHashMapCodec extends WorldInfoHashMap<LongHashMapCodec, Long> {
    @Override
    public void encode (JsonDataWriter writer) {
        forEach(writer::writeLong);
    }

    @Override
    public void decode (JsonDataReader reader) {
        reader.getParse().forEach(pair -> {
            put(pair.name(), pair.asLong());
        });
    }
}
