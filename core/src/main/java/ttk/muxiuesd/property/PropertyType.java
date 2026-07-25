package ttk.muxiuesd.property;

import game.muxiuesd.bedrockcore.app.interfaces.data.IReadData;
import game.muxiuesd.bedrockcore.app.interfaces.data.IWriteData;
import game.muxiuesd.bedrockcore.serialization.Codec;
import game.muxiuesd.bedrockcore.serialization.Codecable;

/**
 * 属性类型，需要实现写入数据的逻辑接口：{@link IWriteData}
 * 读取接口传入的数据是一整个属性map的数据，根据id来获取对应的属性值
 * */
public abstract class PropertyType<T> implements Codecable<PropertyType<T>>, IWriteData<T>, IReadData<T> {
    private String id;

    public String getId () {
        return this.id;
    }

    public PropertyType<T> setId (String id) {
        this.id = id;
        return this;
    }

    /**
     * 获取属性类型的值的编辑解码器
     * */
    abstract public Codec<T> getValueCodec ();
}
