package ttk.muxiuesd.data.abs;

import ttk.muxiuesd.interfaces.data.DataReadable;
import ttk.muxiuesd.interfaces.data.DataWriteable;
import ttk.muxiuesd.property.PropertyType;

/**
 * 属性数据
 * */
public abstract class PropertiesDataMap<D extends PropertiesDataMap> implements DataReadable, DataWriteable {
    public abstract <T> D add (PropertyType<T> type, T value);
    public abstract <T> D remove (PropertyType<T> type);
    public abstract <T> T get (PropertyType<T> type);
    public abstract boolean contain (PropertyType<?> type);
    public abstract D copy ();
}
