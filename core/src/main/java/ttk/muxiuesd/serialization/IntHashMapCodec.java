package ttk.muxiuesd.serialization;

import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.serialization.abs.WorldInfoHashMap;

/**
 * 可以编解码的int类型的HashMap
 * */
public class IntHashMapCodec extends WorldInfoHashMap<IntHashMapCodec, Integer> {

    @Override
    public void encode (JsonDataWriter writer) {
        forEach(writer::writeInt);
    }

    @Override
    public void decode (JsonDataReader reader) {
        reader.getParse().forEach(pair -> {
            //这么写才不会出错
            put(pair.name(), pair.asInt());
        });
    }
}
