package ttk.muxiuesd.data.abs;

import ttk.muxiuesd.interfaces.data.DataReader;
import ttk.muxiuesd.interfaces.data.DataWriter;
import ttk.muxiuesd.property.PropertyType;

import java.util.function.BiConsumer;

/**
 * 属性数据map的抽象接口
 * */
public abstract class PropertiesDataMap<D extends PropertiesDataMap<?, W, R>, W extends DataWriter<?>, R extends DataReader<?>>{
    public abstract <T> D add (PropertyType<T> type, T value);
    public abstract <T> D remove (PropertyType<T> type);
    public abstract <T> T get (PropertyType<T> type);

    /**
     * 判断是否持有某一个属性
     * */
    public abstract boolean contain (PropertyType<?> type);

    /**
     * 把持有的所有属性全部复制一份出来
     * */
    public abstract D copy ();

    /**
     * 判断所持有的所有属性是否相等（数量，种类，对应的值）
     * */
    public abstract boolean equals (PropertiesDataMap<?, ?, ?> other);

    /**
     * 属性的数量
     * */
    public abstract int getCount ();

    public abstract void forEach (BiConsumer<? super PropertyType, Object> action);

    public abstract void write (W writer);
    public abstract void read (R reader);
}
