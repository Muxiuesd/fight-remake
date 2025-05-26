package ttk.muxiuesd.property;

import ttk.muxiuesd.interfaces.data.IWriteData;

/**
 * 属性类型，需要实现写入数据的逻辑接口：{@link IWriteData}
 * */
public abstract class PropertyType<T> implements IWriteData<T> {
    private String name;

    public String getName () {
        return this.name;
    }

    public PropertyType<T> setName (String name) {
        this.name = name;
        return this;
    }
}
