package ttk.muxiuesd.data;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import ttk.muxiuesd.interfaces.data.DataWriter;

import java.io.StringWriter;

/**
 * Json格式的数据写入类
 * */
public class JsonDataWriter implements DataWriter<Json> {
    private Json writer;
    private static final JsonWriter.OutputType outputType = JsonWriter.OutputType.json;

    public JsonDataWriter () {
        this.writer = new Json();
        this.writer.setWriter(new JsonWriter(new StringWriter()));
        this.writer.setOutputType(outputType);
        this.writer.getWriter().setOutputType(outputType);
    }

    /**
     * 开始一个有名称的json对象
     * */
    public void objStart (String objName) {
        this.writer.writeObjectStart(objName);
    }

    /**
     * 开始一个无名称的json对象
     * */
    public void objStart () {
        this.writer.writeObjectStart();
    }

    /**
     * 结束当前的json对象
     * */
    public void objEnd () {
        this.writer.writeObjectEnd();
    }

    @Override
    public JsonDataWriter writeInt (String key, int value) {
        this.writer.writeValue(key, value);
        return this;
    }

    @Override
    public JsonDataWriter writeLong (String key, long value) {
        this.writer.writeValue(key, value);
        return this;
    }

    @Override
    public JsonDataWriter writeFloat (String key, float value) {
        this.writer.writeValue(key, value);
        return this;
    }

    @Override
    public JsonDataWriter writeDouble (String key, double value) {
        this.writer.writeValue(key, value);
        return this;
    }

    @Override
    public JsonDataWriter writeBoolean (String key, boolean value) {
        this.writer.writeValue(key, value);
        return this;
    }

    @Override
    public JsonDataWriter writeChar (String key, char value) {
        this.writer.writeValue(key, value);
        return this;
    }

    @Override
    public JsonDataWriter writeByte (String key, byte value) {
        this.writer.writeValue(key, value);
        return this;
    }

    @Override
    public JsonDataWriter writeShort (String key, short value) {
        this.writer.writeValue(key, value);
        return this;
    }

    @Override
    public JsonDataWriter writeString (String key, String value) {

        this.writer.writeValue(key, value);
        return this;
    }

    @Override
    public Json getWriter () {
        return this.writer;
    }

    @Override
    public JsonDataWriter setWriter (Json writer) {
        if (writer != null) {
            this.writer = writer;
        }
        return this;
    }
}
