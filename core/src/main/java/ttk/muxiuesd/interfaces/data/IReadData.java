package ttk.muxiuesd.interfaces.data;

/**
 * 读取数据的逻辑接口
 * @param <T> 被读取数据的类型
 * */
public interface IReadData<T> {
    T read(DataReader<?> reader, String dataKey);
}
