package ttk.muxiuesd.property;

import ttk.muxiuesd.interfaces.data.IReadData;
import ttk.muxiuesd.interfaces.data.IWriteData;

/**
 * 属性类型，需要实现写入数据的逻辑接口：{@link IWriteData}
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
