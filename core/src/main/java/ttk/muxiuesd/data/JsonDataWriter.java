package ttk.muxiuesd.data;

import com.badlogic.gdx.utils.JsonWriter;
import ttk.muxiuesd.interfaces.data.DataWriter;

import java.io.IOException;
import java.io.StringWriter;

/**
 * Json格式的数据写入类
 * */
public class JsonDataWriter implements DataWriter<JsonWriter> {
    private JsonWriter writer;

    public JsonDataWriter () {
        this.writer = new JsonWriter(new StringWriter());
        try {
            this.writer.object();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonDataWriter writeInt (String key, int value) {
        try {
            writer.set(key, value);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public JsonDataWriter writeLong (String key, long value) {
        try {
            writer.set(key, value);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public JsonDataWriter writeFloat (String key, float value) {
        try {
            writer.set(key, value);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public JsonDataWriter writeDouble (String key, double value) {
        try {
            writer.set(key, value);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public JsonDataWriter writeBoolean (String key, boolean value) {
        try {
            writer.set(key, value);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public JsonDataWriter writeChar (String key, char value) {
        try {
            writer.set(key, value);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public JsonDataWriter writeByte (String key, byte value) {
        try {
            writer.set(key, value);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public JsonDataWriter writeShort (String key, short value) {
        try {
            writer.set(key, value);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public JsonDataWriter writeString (String key, String value) {
        try {
            writer.set(key, value);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    @Override
    public JsonWriter getWriter () {
        return this.writer;
    }

    @Override
    public JsonDataWriter setWriter (JsonWriter writer) {
        if (writer != null) {
            this.writer = writer;
        }
        return this;
    }
}
