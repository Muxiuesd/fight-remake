package ttk.muxiuesd.world.entity.abs;

import com.badlogic.gdx.utils.ObjectMap;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.Codecable;
import game.muxiuesd.bedrockcore.serialization.DataResult;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.serialization.codecs.CodecJsonPropertiesMap;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 实体属性的修饰表：原始值 + 当前值
 * <p>
 * 键是 {@link PropertyType}，值可以是任意类型（数值、布尔、标识符……）。
 * 原始值是实体固定的数据（构造器或者读档写入），当前值是被状态效果改写后的值；
 * 效果结束时用 {@link #restore(PropertyType)} 把当前值还原成原始值。
 * <p>
 * 原始值一直留在这里，所以不需要像旧的速度提升效果那样额外保存"被改之前是多少"，
 * 也就不会出现"存档后再进来原始值找不到"的问题。
 */
public final class AttributesModifier implements Codecable<AttributesModifier> {
    /**
     * 属性表的编解码器
     * <p>
     * 结构是 {@code {"base": {属性id: 值…}, "current": {属性id: 值…}}}：
     * base 是原始值；current 只存被改过的当前值。
     * <p>
     * 当前值也要落盘：像当前血量这种数据不是从原始值派生出来的（原始值是血量上限），
     * 只存原始值的话读档就只剩满血。状态效果改出来的当前值存下来也没关系，
     * 读档后 {@code LivingEntity.setEffects} 会按剩余时长重新应用一次，把它算回正确的值。
     * <p>
     * 旧存档的属性表只有一层（键直接是属性id），解码时按"只有原始值"处理
     * */
    public static final Codec<AttributesModifier> CODEC = new Codec<>() {
        private static final String BASE = "base";
        private static final String CURRENT = "current";

        @Override
        public RawObject encode (AttributesModifier modifier) {
            Map<String, Object> encoded = new LinkedHashMap<>();
            encoded.put(BASE, CodecJsonPropertiesMap.MAP_CODEC.encode(modifier.getBaseValues()).unwrap());
            encoded.put(CURRENT, CodecJsonPropertiesMap.MAP_CODEC.encode(modifier.getModifiedValues()).unwrap());
            return RawObject.ofMap(encoded);
        }

        @Override
        @SuppressWarnings("unchecked")
        public DataResult<AttributesModifier> decode (RawObject input) {
            AttributesModifier modifier = new AttributesModifier();
            if (!input.isMap()) return DataResult.error("实体属性表应该是一个对象", modifier);
            Map<String, Object> rawMap = input.asMap().get();

            //旧存档：整个对象就是原始值表，没有 base/current 两层
            boolean nested = rawMap.containsKey(BASE) || rawMap.containsKey(CURRENT);
            DataResult<LinkedHashMap<PropertyType, Object>> baseResult = nested
                ? decodeMap(rawMap.get(BASE))
                : CodecJsonPropertiesMap.MAP_CODEC.decode(input);
            DataResult<LinkedHashMap<PropertyType, Object>> currentResult = nested
                ? decodeMap(rawMap.get(CURRENT))
                : DataResult.success(new LinkedHashMap<>());

            //先原始值再当前值：先把基准铺好，再盖被改过的当前值
            baseResult.result().ifPresent(map -> map.forEach((type, value) ->
                modifier.setBase((PropertyType<Object>) type, value)));
            currentResult.result().ifPresent(map -> map.forEach((type, value) ->
                modifier.set((PropertyType<Object>) type, value)));

            if (baseResult.isSuccess() && currentResult.isSuccess()) return DataResult.success(modifier);

            StringBuilder errors = new StringBuilder();
            baseResult.error().ifPresent(errors::append);
            currentResult.error().ifPresent(errors::append);
            return DataResult.error(errors.length() == 0 ? "实体属性解码失败" : errors.toString(), modifier);
        }

        /**
         * 解一层属性表；这一层没有时返回空表（旧存档没有 current 层是正常的）
         * */
        private DataResult<LinkedHashMap<PropertyType, Object>> decodeMap (Object rawValue) {
            if (rawValue == null) return DataResult.success(new LinkedHashMap<>());
            return CodecJsonPropertiesMap.MAP_CODEC.decode(Codec.wrap(rawValue));
        }
    };

    /// 原始值
    private final ObjectMap<PropertyType<?>, Object> baseValues = new ObjectMap<>();
    /// 当前值（被状态效果改写）
    private final ObjectMap<PropertyType<?>, Object> currentValues = new ObjectMap<>();

    /**
     * 获取原始值，没有登记过返回默认值
     * */
    public <T> T getBase (PropertyType<T> type, T defaultValue) {
        Object value = this.baseValues.get(type);
        return value == null ? defaultValue : cast(value);
    }

    /**
     * 获取当前值
     * <p>
     * 当前值缺省就等于原始值：只有被状态效果改过才会有自己的条目。
     * 原始值也没登记过时（比如属性键写错了）才用默认值兜底，避免 {@code Float} 拆箱空指针
     * */
    public <T> T getOrDefault (PropertyType<T> type, T defaultValue) {
        Object value = this.currentValues.get(type);
        if (value != null) return cast(value);

        value = this.baseValues.get(type);
        return value != null ? cast(value) : defaultValue;
    }

    /**
     * 设置原始值
     * <p>
     * 只写原始值，不碰当前值：当前值缺省就取原始值（见 {@link #getOrDefault(PropertyType, Object)}），
     * 所以当前值表里只留"被改过"的项，写原始值不会顺手把正在生效的临时修改清掉
     * */
    public <T> AttributesModifier setBase (PropertyType<T> type, T base) {
        this.baseValues.put(type, base);
        return this;
    }

    /**
     * 只改当前值，不动原始值（状态效果开始时用）
     * */
    public <T> AttributesModifier set (PropertyType<T> type, T current) {
        this.currentValues.put(type, current);
        return this;
    }

    /**
     * 当前值还原成原始值（状态效果结束时用）
     * <p>
     * 直接移除当前值即可：当前值缺省就是原始值，不用往表里拷一份
     * */
    public <T> AttributesModifier restore (PropertyType<T> type) {
        this.currentValues.remove(type);
        return this;
    }

    /**
     * 导出原始值表，供编解码器使用
     * */
    public LinkedHashMap<PropertyType, Object> getBaseValues () {
        LinkedHashMap<PropertyType, Object> map = new LinkedHashMap<>(this.baseValues.size);
        this.baseValues.forEach(entry -> map.put(entry.key, entry.value));
        return map;
    }

    /**
     * 导出被改过的当前值（与原始值不同的那些），供编解码器使用
     * <p>
     * 当前值表里本来就只有被改过的项，这里再和原始值比一次，避免"改回原值但没移除"的项也写进存档
     * */
    public LinkedHashMap<PropertyType, Object> getModifiedValues () {
        LinkedHashMap<PropertyType, Object> map = new LinkedHashMap<>();
        this.currentValues.forEach(entry -> {
            Object base = this.baseValues.get(entry.key);
            if (!Objects.equals(base, entry.value)) map.put(entry.key, entry.value);
        });
        return map;
    }

    @Override
    public Codec<AttributesModifier> getCodec () {
        return CODEC;
    }


    private static <T> T cast (Object value) {
        return (T) value;
    }
}
