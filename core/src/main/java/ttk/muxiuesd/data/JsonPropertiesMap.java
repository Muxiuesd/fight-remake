package ttk.muxiuesd.data;

import game.muxiuesd.bedrockcore.app.interfaces.ShallowCopyable;
import game.muxiuesd.bedrockcore.data.JsonDataReader;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import game.muxiuesd.bedrockcore.serialization.Codec;
import ttk.muxiuesd.data.abs.PropertiesDataMap;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.serialization.codecs.CodecJsonPropertiesMap;

import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;

/**
 * Json格式的属性数据map，各种属性将会按照json格式来读取和写入
 * */
public class JsonPropertiesMap extends PropertiesDataMap<JsonPropertiesMap, JsonDataWriter, JsonDataReader> {

    private LinkedHashMap<PropertyType, Object> propertiesMap;

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
     * <p>
     * 属性值副本策略：
     * <ul>
     *   <li><b>实现 {@link ShallowCopyable} 的值</b>：调用其 {@code copy()} 生成独立副本
     *       （如 {@code CatsHolder}——有可变的 map，须隔离）。</li>
     *   <li><b>未实现 {@link ShallowCopyable} 的值</b>：直接共享引用对象，不复制
     *       （基本类型 Integer/Boolean/Float/String 不可变；无状态引用如 AudioHolder；
     *       BlockSounds 全局单例；Entity 实体引用本就应共享。共享引用是规定语义，非别名隐患）。</li>
     * </ul>
     * 即：只有 {@code implements ShallowCopyable} 的类才会调用其 {@code copy()}，其余一律共享引用。
     */
    @Override
    public JsonPropertiesMap copy () {
        JsonPropertiesMap map = new JsonPropertiesMap();
        this.propertiesMap.forEach((key, value) -> {
            if (value == null) {
                //null 值保留键（值为 null）
                map.add(key, null);
            } else if (value instanceof ShallowCopyable<?> shallowCopyableValue) {
                //实现了浅拷贝接口的值：调用 copy() 生成独立副本
                map.add(key, shallowCopyableValue.copy());
            } else {
                //未实现浅拷贝接口的值：直接共享引用对象（规定语义，见类注释）
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
            //没有这个属性就不相等
            if (!other.contain(key)) {
                result.set(false);
                return;
            }
            //属性值的 null 安全比较：都为 null 视为相等（避免 null.equals(...) 抛 NPE）
            Object otherValue = other.get(key);
            if (!Objects.equals(value, otherValue)) result.set(false);
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
            //查找注册的属性类型（未知属性类型跳过，与 CodecJsonPropertiesMap 的降级行为一致）
            PropertyType propertyType = Registries.PROPERTY_TYPE.getOrNull(typeId);
            if (propertyType == null) {
                game.muxiuesd.bedrockcore.util.Log.error(this.getClass().getName(),
                    "属性类型：" + typeId + " 未注册，已跳过该属性（旧存档数据）");
                return;
            }
            Object value = propertyType.read(reader, typeId);
            this.add(propertyType, value);
        });
    }

    @Override
    public LinkedHashMap<PropertyType, Object> getPropertiesMap () {
        return this.propertiesMap;
    }

    @Override
    public void setPropertiesMap (LinkedHashMap<PropertyType, Object> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }

    @Override
    public Codec<JsonPropertiesMap> getCodec () {
        return CodecJsonPropertiesMap.CODEC;
    }
}
