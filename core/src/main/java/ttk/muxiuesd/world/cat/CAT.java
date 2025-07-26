package ttk.muxiuesd.world.cat;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.data.JsonDataWriter;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * 自定义属性标签：custom attribute tag （CAT）
 * */
public class CAT {
    private final HashMap<String, Object> entries = new LinkedHashMap<>();

    public CAT set (String key, Object value) {
        this.getMap().put(key, value);
        return this;
    }

    public <T> T get (String key, Class<T> type) {
        return (T) this.getMap().get(key);
    }

    public void write (JsonDataWriter writer) {
        this.getMap().forEach((key, value) -> writer.getWriter().writeValue(key, value));
    }

    public void read (JsonValue values) {

    }

    public HashMap<String, Object> getMap () {
        return this.entries;
    }
}
