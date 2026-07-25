package ttk.muxiuesd.property;

import game.muxiuesd.bedrockcore.app.interfaces.data.DataReader;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataWriter;
import game.muxiuesd.bedrockcore.serialization.Codec;
import ttk.muxiuesd.registrant.Registries;

/**
 * 默认的布尔属性类型
 * */
public class DefaultBoolPropertyType extends PropertyType<Boolean> {
    public static final Codec<PropertyType<Boolean>> CODEC = Codec.STRING.xmap(
        (id) -> {
            // 解码：id 字符串 → PropertyType<?>（从注册表获取）
            return (PropertyType<Boolean>) Registries.PROPERTY_TYPE.get(id);
        },
        PropertyType::getId    // 编码：PropertyType<?> → id 字符串
    );


    @Override
    public void write (DataWriter<?> writer, Boolean data) {
        writer.writeBoolean(getId(), data);
    }

    @Override
    public Boolean read (DataReader<?> reader, String dataKey) {
        return reader.readBoolean(dataKey);
    }

    @Override
    public Codec<PropertyType<Boolean>> getCodec () {
        return CODEC;
    }

    @Override
    public Codec<Boolean> getValueCodec () {
        return Codec.BOOL;
    }
}
