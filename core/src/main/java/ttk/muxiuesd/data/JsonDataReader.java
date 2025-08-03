package ttk.muxiuesd.data;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.interfaces.data.DataReader;
import ttk.muxiuesd.util.Log;

/**
 * Json格式的数据读取类
 * */
public class JsonDataReader implements DataReader<JsonReader> {
    public static final String TAG = JsonDataReader.class.getName();
    private JsonReader reader;
    private JsonValue parse;

    public JsonDataReader(JsonValue parse) {
        this.reader = new JsonReader();
        this.parse = parse;
    }
    public JsonDataReader (String jsonString) {
        this.reader = new JsonReader();
        this.parse  = this.reader.parse(jsonString);
    }

    /**
     * 读obj
     * */
    public JsonValue readObj (String key) {
        if (!this.getParse().has(key)) {
            Log.error(TAG, "json中不存在名为：" + key + " 的obj！！！" +
                "原文：\n" + this.getParse().toString());
            throw new IllegalArgumentException(key);
        }
        return this.getParse().get(key);
    }

    /**
     * 读array
     * */
    public JsonValue readArray (String key) {
        if (!this.getParse().has(key)) {
            Log.error(TAG, "json中不存在名为：" + key + " 的array！！！" +
                "原文：\n" + this.getParse().toString());
            throw new IllegalArgumentException(key);
        }
        return this.getParse().get(key);
    }

    @Override
    public int readInt (String key) {
        this.check(key);
        return this.getParse().getInt(key);
    }

    @Override
    public long readLong (String key) {
        this.check(key);
        return this.getParse().getLong(key);
    }

    @Override
    public float readFloat (String key) {
        this.check(key);
        return this.getParse().getFloat(key);
    }

    @Override
    public double readDouble (String key) {
        this.check(key);
        return this.getParse().getDouble(key);
    }

    @Override
    public boolean readBoolean (String key) {
        this.check(key);
        return this.getParse().getBoolean(key);
    }

    @Override
    public char readChar (String key) {
        this.check(key);
        return this.getParse().getChar(key);
    }

    @Override
    public byte readByte (String key) {
        this.check(key);
        return this.getParse().getByte(key);
    }

    @Override
    public short readShort (String key) {
        this.check(key);
        return this.getParse().getShort(key);
    }

    @Override
    public String readString (String key) {
        this.check(key);
        return this.getParse().getString(key);
    }


    public JsonValue getParse() {
        return this.parse;
    }

    @Override
    public JsonReader getReader () {
        return this.reader;
    }

    @Override
    public JsonDataReader setReader (JsonReader reader) {
        if (reader != null) {
            this.reader = reader;
        }
        return this;
    }

    private void check (String key) {
        if (!this.getParse().has(key)) {
            Log.error(TAG, "json中不存在名为：" + key + " 的值！！！" +
                "原文：\n" + this.getParse().toString());
            throw new IllegalArgumentException(key);
        }
    }
}
