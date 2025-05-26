package ttk.muxiuesd.data;

import ttk.muxiuesd.interfaces.data.DataReader;
import ttk.muxiuesd.interfaces.data.DataWriter;
import ttk.muxiuesd.property.PropertyType;

import java.util.LinkedHashMap;

/**
 * Json格式的属性数据映射
 * */
public class JsonPropertiesMap extends PropertiesDataMap<JsonPropertiesMap> {
    private final LinkedHashMap<PropertyType, Object> propertiesMap;


    public JsonPropertiesMap() {
        this.propertiesMap = new LinkedHashMap<>();
    }

    public JsonPropertiesMap(LinkedHashMap<PropertyType, Object> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }

    @Override
    public <T> JsonPropertiesMap add (PropertyType<T> type, T value) {
        this.propertiesMap.put(type, value);
        return this;
    }

    @Override
    public <T> JsonPropertiesMap remove (PropertyType<T> type) {
        this.propertiesMap.remove(type);
        return this;
    }

    @Override
    public <T> T get (PropertyType<T> type) {
        return (T) this.propertiesMap.get(type);
    }

    @Override
    public JsonPropertiesMap copy () {
        return new JsonPropertiesMap((LinkedHashMap<PropertyType, Object>) propertiesMap.clone());
    }

    @Override
    public  void read (DataReader<?> reader) {
    }

    @Override
    public void write (DataWriter<?> writer) {
        this.propertiesMap.forEach((propertyType, value) -> propertyType.write(writer, value));
    }
}
