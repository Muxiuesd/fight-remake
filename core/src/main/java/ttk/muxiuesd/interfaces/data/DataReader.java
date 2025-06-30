package ttk.muxiuesd.interfaces.data;

/**
 * 数据读取接口
 * @param <T> 数据读取实现类
 * */
public interface DataReader<T> {
    int readInt(String key);
    long readLong(String key);
    float readFloat(String key);
    double readDouble(String key);
    boolean readBoolean(String key);
    char readChar(String key);
    byte readByte(String key);
    short readShort(String key);
    String readString(String key);

    T getReader ();
    DataReader<T> setReader (T reader);
}
