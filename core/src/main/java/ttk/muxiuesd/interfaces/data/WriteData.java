package ttk.muxiuesd.interfaces.data;

/**
 * 写入数据的逻辑接口
 * */
public interface WriteData<T> {
    void write(DataWriter writer, T data);
}
