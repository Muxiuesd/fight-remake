package ttk.muxiuesd.data;

import ttk.muxiuesd.data.abs.PropertiesDataMap;
import ttk.muxiuesd.interfaces.data.DataReader;
import ttk.muxiuesd.interfaces.data.DataWriter;
import ttk.muxiuesd.property.PropertyType;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Json格式的属性数据map
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
    public boolean contain (PropertyType<?> type) {
        return this.propertiesMap.containsKey(type);
    }

    @Override
    public JsonPropertiesMap copy () {
        return new JsonPropertiesMap((LinkedHashMap<PropertyType, Object>) propertiesMap.clone());
    }

    @Override
    public boolean equals (PropertiesDataMap<?> other) {
        if (this.getCount() != other.getCount()) return false;
        AtomicBoolean result = new AtomicBoolean(true);
        this.propertiesMap.forEach((key, value) -> {
            //如果没有这个属性或者有这个属性但值对不上
            if (!other.contain(key) || !other.get(key).equals(value)) result.set(false);

        });

        return result.get();
    }

    @Override
    public int getCount () {
        return this.propertiesMap.size();
    }

    @Override
    public  void read (DataReader<?> reader) {
    }

    @Override
    public void write (DataWriter<?> writer) {
        this.propertiesMap.forEach((propertyType, value) -> propertyType.write(writer, value));
    }
}
