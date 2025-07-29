package ttk.muxiuesd.property;

import ttk.muxiuesd.interfaces.data.IReadData;
import ttk.muxiuesd.interfaces.data.IWriteData;

/**
 * 属性类型，需要实现写入数据的逻辑接口：{@link IWriteData}
 * 读取接口传入的数据是一整个属性map的数据，根据id来获取对应的属性值
 * */
public abstract class PropertyType<T> implements IWriteData<T>, IReadData<T> {
    private String id;

    public String getId () {
        return this.id;
    }

    public PropertyType<T> setId (String id) {
        this.id = id;
        return this;
    }
}
