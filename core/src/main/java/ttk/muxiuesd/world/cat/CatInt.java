package ttk.muxiuesd.world.cat;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.data.JsonDataWriter;

public class CatInt extends CatValue<Integer>{
    public CatInt (int value) {
        super(value);
    }

    @Override
    public void write (String key, JsonDataWriter writer) {
        writer.writeInt(key, get());
    }

    @Override
    public void read (String key, JsonValue values) {
        set(values.getInt(key));
    }

    @Override
    public CatValue<Integer> newSelf () {
        return new CatInt(get());
    }
}
