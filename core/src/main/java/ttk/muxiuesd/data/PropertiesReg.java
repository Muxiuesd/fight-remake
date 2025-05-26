package ttk.muxiuesd.data;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.PropertyType;

import java.util.LinkedHashMap;

/**
 * 属性注册
 * */
public class PropertiesReg {
    public static final LinkedHashMap<String, Identifier> idMap = new LinkedHashMap<>();
    public static final LinkedHashMap<Identifier, PropertyType<?>> table = new LinkedHashMap<>();

    public static <T> PropertyType<T> register (String name, PropertyType<T> type) {
        String id = Fight.getId(name);
        if (idMap.containsKey(id)) {
            throw new IllegalArgumentException("输入的ID：" + id + " 已存在！！！");
        }
        Identifier identifier = new Identifier(id);
        return register(identifier, type);
    }

    public static <T> PropertyType<T> register (Identifier identifier, PropertyType<T> type) {
        if (table.containsKey(identifier)) {
            throw new IllegalArgumentException();
        }
        idMap.put(identifier.getId(), identifier);
        table.put(identifier, type);
        return type;
    }
}
