package ttk.muxiuesd.serialization;

import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.data.JsonPropertiesMap;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.block.abs.Block;

import java.util.Optional;

/**
 * 方块的属性的编解码器
 * */
public class BlockPropertyCodec extends JsonCodec<Block.Property> {
    @Override
    public void encode (Block.Property property, JsonDataWriter dataWriter) {
        ((JsonPropertiesMap)property.getPropertiesMap()).write(dataWriter);
    }

    @Override
    public Optional<Block.Property> parse (JsonDataReader dataReader) {
        Block.Property property = Block.createProperty();
        JsonPropertiesMap map = new JsonPropertiesMap();
        map.read(dataReader);
        property.setPropertiesMap(map);
        return Optional.of(property);
    }
}
