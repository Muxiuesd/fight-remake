package ttk.muxiuesd.interfaces.data;

/**
 * 写入数据的逻辑接口
 * @param <T> 被写入数据的类型
 * */
public interface IWriteData<T> {
    void write(DataWriter<?> writer, T data);
}
