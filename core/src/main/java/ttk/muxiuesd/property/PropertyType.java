package ttk.muxiuesd.property;

import game.muxiuesd.bedrockcore.app.interfaces.data.IReadData;
import game.muxiuesd.bedrockcore.app.interfaces.data.IWriteData;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.Codecable;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;

/**
 * 属性类型，需要实现写入数据的逻辑接口：{@link IWriteData}
 * 读取接口传入的数据是一整个属性map的数据，根据id来获取对应的属性值
 * */
public abstract class PropertyType<T> implements Codecable<PropertyType<T>>, IWriteData<T>, IReadData<T> {
    public final Codec<PropertyType<T>> CODEC = Codec.STRING.xmap(
        (id) -> {
            // 解码：id 字符串 → PropertyType<?>（从注册表获取）
            return (PropertyType<T>) Registries.PROPERTY_TYPE.get(id);
        },
        PropertyType::getId    // 编码：PropertyType<?> → id 字符串
    );


    private Identifier identifier;

    public String getId () {
        return this.getIdentifier() == null ? null : this.getIdentifier().getID();
    }

    public Identifier getIdentifier () {
        return this.identifier;
    }

    public PropertyType<T> setIdentifier (Identifier identifier) {
        //Identifier 只在注册阶段给定，注册过后不允许修改
        if (this.identifier != null && !this.identifier.equals(identifier)) {
            throw new IllegalStateException("Identifier 已设置，禁止修改！属性类型：" + this.identifier.getID() + " -> " + identifier.getID());
        }
        this.identifier = identifier;
        return this;
    }

    @Override
    public Codec<PropertyType<T>> getCodec () {
        return CODEC;
    }

    /**
     * 获取属性类型的值的编辑解码器
     * */
    abstract public Codec<T> getValueCodec ();
}
