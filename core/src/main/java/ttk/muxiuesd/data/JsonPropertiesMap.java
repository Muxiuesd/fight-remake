package ttk.muxiuesd.data;

import com.badlogic.gdx.utils.Json;
import ttk.muxiuesd.interfaces.PropertyType;

import java.util.LinkedHashMap;

/**
 * Json格式的属性数据
 * */
public class JsonPropertiesMap extends PropertiesData<Json, Json> {
    private LinkedHashMap<PropertyType<?>, Object> propertiesMap = new LinkedHashMap<>();

    @Override
    public <T> PropertiesData<Json, Json> add (PropertyType<T> type, T value) {
        this.propertiesMap.put(type, value);
        return this;
    }

    @Override
    public <T> PropertiesData<Json, Json> remove (PropertyType<T> type) {
        this.propertiesMap.remove(type);
        return this;
    }

    @Override
    public <T> T get (PropertyType<T> type) {
        return (T) this.propertiesMap.get(type);
    }

    @Override
    public void read (Json reader) {
    }

    @Override
    public void write (Json writer) {

    }
}
