package ttk.muxiuesd.serialization;

import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.data.JsonPropertiesMap;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.entity.abs.Entity;

import java.util.Optional;

/**
 * 实体属性的编解码器
 * */
public class EntityPropertyCodec extends JsonCodec<Entity.Property> {
    @Override
    public void encode (Entity.Property property, JsonDataWriter dataWriter) {
        ((JsonPropertiesMap)property.getPropertiesMap()).write(dataWriter);
    }
    @Override
    public Optional<Entity.Property> parse (JsonDataReader dataReader) {
        Entity.Property property = new Entity.Property();
        JsonPropertiesMap map = new JsonPropertiesMap();
        map.read(dataReader);
        property.setPropertiesMap(map);
        return Optional.of(property);
    }
}
