package ttk.muxiuesd.world.cat;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.data.JsonDataWriter;

public class CatBoolean extends CatValue<Boolean>{
    public CatBoolean (boolean value) {
        super(value);
    }

    @Override
    public void write (String key, JsonDataWriter writer) {
        writer.writeBoolean(key, get());
    }

    @Override
    public void read (String key, JsonValue values) {
        set(values.getBoolean(key));
    }

    @Override
    public CatValue<Boolean> newSelf () {
        return new CatBoolean(get());
    }
}
