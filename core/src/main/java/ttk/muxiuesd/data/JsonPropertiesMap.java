package ttk.muxiuesd.data;

import game.muxiuesd.bedrockcore.app.interfaces.ShallowCopyable;
import ttk.muxiuesd.data.abs.PropertiesDataMap;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.registrant.Registries;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

/**
 * Json格式的属性数据map，各种属性将会按照json格式来读取和写入
 * */
public class JsonPropertiesMap extends PropertiesDataMap<JsonPropertiesMap, JsonDataWriter, JsonDataReader> {
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

    /**
     * 复制一份属性
     * */
    @Override
    public JsonPropertiesMap copy () {
        JsonPropertiesMap map = new JsonPropertiesMap();
        this.propertiesMap.forEach((key, value) -> {
            //如果是可浅拷贝的值就调用浅拷贝（只复制值，是不同的实例对象）
            if (value instanceof ShallowCopyable<?> shallowCopyableValue) {
                map.add(key, shallowCopyableValue.copy());
            }else {
                //不是浅拷贝的值就直接添加（相同的实例）
                map.add(key, value);
            }
        });
        return map;
    }

    /**
     * 检查属性map是否持有相同的属性
     * */
    @Override
    public boolean equals (PropertiesDataMap<?, ?, ?> other) {
        if (this.getCount() != other.getCount()) return false;
        AtomicBoolean result = new AtomicBoolean(true);
        this.propertiesMap.forEach((key, value) -> {
            //如果没有这个属性或者有这个属性的相等判断对不上（最好对于传入的属性值的类单独实现equals）
            if (!other.contain(key) || !other.get(key).equals(value)) result.set(false);
        });

        return result.get();
    }

    @Override
    public int getCount () {
        return this.propertiesMap.size();
    }

    @Override
    public void forEach (BiConsumer<? super PropertyType, Object> action) {
        if (action == null) return;
        this.propertiesMap.forEach(action);
    }

    @Override
    public void write (JsonDataWriter writer) {
        this.forEach((propertyType, value) -> {
            propertyType.write(writer, value);
        });
    }

    /**
     * 从json中读取属性的数据
     * */
    @Override
    public void read (JsonDataReader reader) {
        reader.getParse().forEach(propertyTypeValue -> {
            String typeId = propertyTypeValue.name();
            //查找注册的属性类型
            PropertyType propertyType = Registries.PROPERTY_TYPE.get(typeId);
            Object value = propertyType.read(reader, typeId);
            this.add(propertyType, value);
        });
    }
}
