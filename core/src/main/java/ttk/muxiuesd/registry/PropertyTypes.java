package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.PropertyType;
import ttk.muxiuesd.registrant.Registries;

public class PropertyTypes {

    public static final PropertyType<Integer> ITEM_MAX_COUNT = register("item_max_count",
        (writer, data) -> {
        writer.writeInt("item_max_count", data.intValue());
    });
    public static final PropertyType<String> ITEM_USE_SOUND_ID = register("item_use_sound_id",
        (writer, data) -> {
        writer.writeString("item_use_sound_id", data);
    });

    public static <T> PropertyType<T> register (String name, PropertyType<T> type) {
        Identifier identifier = new Identifier(Fight.getId(name));
        return (PropertyType<T>) Registries.PROPERTY_TYPE.register(identifier, type);
    }
}
