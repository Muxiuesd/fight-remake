package ttk.muxiuesd.data;

import ttk.muxiuesd.interfaces.PropertyType;
import ttk.muxiuesd.interfaces.data.DataReader;
import ttk.muxiuesd.interfaces.data.DataWriter;

/**
 * 属性数据
 * */
public abstract class PropertiesDataMap<R, W, D extends PropertiesDataMap> implements DataReader<R>, DataWriter<W> {
    public abstract <T> D add (PropertyType<T> type, T value);
    public abstract <T> D remove (PropertyType<T> type);
    public abstract <T> T get (PropertyType<T> type);

    public abstract D copy ();
}
