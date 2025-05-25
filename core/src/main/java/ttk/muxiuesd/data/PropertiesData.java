package ttk.muxiuesd.data;

import ttk.muxiuesd.interfaces.PropertyType;
import ttk.muxiuesd.interfaces.data.DataReader;
import ttk.muxiuesd.interfaces.data.DataWriter;

/**
 * 属性数据
 * */
public abstract class PropertiesData<R, W> implements DataReader<R>, DataWriter<W> {
    public abstract <T> PropertiesData<R, W> add(PropertyType<T> type, T value);
    public abstract <T> PropertiesData<R, W> remove(PropertyType<T> type);
    public abstract <T> T get(PropertyType<T> type);
}
