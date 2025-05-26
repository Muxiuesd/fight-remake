package ttk.muxiuesd.interfaces;

import ttk.muxiuesd.interfaces.data.WriteData;

/**
 * 属性类型，需要实现写入数据的逻辑接口：{@link WriteData}
 * */
public interface PropertyType<T> extends WriteData<T> {
}
