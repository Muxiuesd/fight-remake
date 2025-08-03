package ttk.muxiuesd.serialization;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.data.JsonDataReader;
import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.data.JsonPropertiesMap;
import ttk.muxiuesd.serialization.abs.JsonCodec;
import ttk.muxiuesd.world.item.abs.Item;

import java.util.Optional;

/**
 * 物品属性的编解码器
 * */
public class ItemPropertyCodec extends JsonCodec<Item.Property> {

    @Override
    public void encode (Item.Property property, JsonDataWriter dataWriter) {
        dataWriter.objStart("property");
        ((JsonPropertiesMap)property.getPropertiesMap()).write(dataWriter);
        dataWriter.objEnd();
    }

    @Override
    public Optional<Item.Property> parse (JsonDataReader dataReader) {
        Item.Property property = new Item.Property();
        JsonPropertiesMap map = new JsonPropertiesMap();
        JsonValue propertyValue = dataReader.readObj("property");
        map.read(new JsonDataReader(propertyValue));
        property.setPropertiesMap(map);

        return Optional.of(property);
    }
}
