package ttk.muxiuesd.interfaces.data;

/**
 * 数据写入接口
 * */
public interface DataWriter<T> {
     DataWriter<T> writeInt(String key, int value);
     DataWriter<T> writeLong(String key, long value);
     DataWriter<T> writeFloat(String key, float value);
     DataWriter<T> writeDouble(String key, double value);
     DataWriter<T> writeBoolean(String key, boolean value);
     DataWriter<T> writeChar(String key, char value);
     DataWriter<T> writeByte(String key, byte value);
     DataWriter<T> writeShort(String key, short value);
     DataWriter<T> writeString(String key, String value);

     T getWriter ();
     DataWriter<T> setWriter (T writer);
}
