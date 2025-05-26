package ttk.muxiuesd.data;

import com.badlogic.gdx.utils.JsonReader;
import ttk.muxiuesd.interfaces.data.DataReader;

public class JsonDataReader implements DataReader<JsonReader> {
    private JsonReader reader;

    @Override
    public int readInt (String key) {
        return 0;
    }

    @Override
    public long readLong (String key) {
        return 0;
    }

    @Override
    public float readFloat (String key) {
        return 0;
    }

    @Override
    public double readDouble (String key) {
        return 0;
    }

    @Override
    public boolean readBoolean (String key) {
        return false;
    }

    @Override
    public char readChar (String key) {
        return 0;
    }

    @Override
    public byte readByte (String key) {
        return 0;
    }

    @Override
    public short readShort (String key) {
        return 0;
    }

    @Override
    public String readString (String key) {
        return "";
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
}
