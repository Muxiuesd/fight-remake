package ttk.muxiuesd.world.entity.abs;

import com.badlogic.gdx.utils.ObjectMap;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.Codecable;
import game.muxiuesd.bedrockcore.serialization.DataResult;
import game.muxiuesd.bedrockcore.serialization.RawObject;
import ttk.muxiuesd.property.PropertyType;
import ttk.muxiuesd.serialization.codecs.CodecJsonPropertiesMap;

import java.util.LinkedHashMap;

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
     * 属性表的编解码器：复用属性表的通用编码，键是属性类型id，值是属性类型自己的值编码
     * <p>
     * 只编码原始值。当前值是状态效果派生出来的中间态，写进存档会和效果的剩余时长对不上。
     * */
    public static final Codec<AttributesModifier> CODEC = new Codec<>() {
        @Override
        public RawObject encode (AttributesModifier modifier) {
            return CodecJsonPropertiesMap.MAP_CODEC.encode(modifier.getBaseValues());
        }

        @Override
        @SuppressWarnings("unchecked")
        public DataResult<AttributesModifier> decode (RawObject input) {
            AttributesModifier modifier = new AttributesModifier();
            DataResult<LinkedHashMap<PropertyType, Object>> decoded =
                CodecJsonPropertiesMap.MAP_CODEC.decode(input);

            //解出来的每一条都是原始值，直接写进原始值表（当前值会同步成原始值）
            decoded.result().ifPresent(map -> map.forEach((type, value) ->
                modifier.setBase((PropertyType<Object>) type, value)));

            if (decoded.isSuccess()) return DataResult.success(modifier);
            return DataResult.error(decoded.error().orElse("实体属性解码失败"), modifier);
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
     * 获取当前值，没有登记过返回默认值
     * <p>
     * 数值属性一律用这个带默认值的读法，避免 {@code Float} 拆箱空指针
     * */
    public <T> T getOrDefault (PropertyType<T> type, T defaultValue) {
        Object value = this.currentValues.get(type);
        if (value != null) return cast(value);

        value = this.baseValues.get(type);
        return value != null ? cast(value) : defaultValue;
    }

    /**
     * 设置原始值，同时把当前值重置成它（写固定值就等于撤销这个属性上的临时修改）
     * */
    public <T> AttributesModifier setBase (PropertyType<T> type, T base) {
        this.baseValues.put(type, base);
        this.currentValues.put(type, base);
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
     * 没有原始值的属性直接移除当前值，避免留下一个没有基准的孤儿条目
     * */
    public <T> AttributesModifier restore (PropertyType<T> type) {
        if (this.baseValues.containsKey(type)) {
            this.currentValues.put(type, this.baseValues.get(type));
        } else {
            this.currentValues.remove(type);
        }
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

    @Override
    public Codec<AttributesModifier> getCodec () {
        return CODEC;
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast (Object value) {
        return (T) value;
    }
}
