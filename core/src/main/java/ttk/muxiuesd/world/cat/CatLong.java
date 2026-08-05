package ttk.muxiuesd.world.cat;

import com.badlogic.gdx.utils.JsonValue;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;

public class CatLong extends CatValue<Long>{
    public CatLong (long value) {
        super(value);
    }

    @Override
    public void write (String key, JsonDataWriter writer) {
        writer.writeLong(key, get());
    }

    @Override
    public void read (String key, JsonValue values) {
        set(values.getLong(key));
    }

    @Override
    public CatValue<Long> newSelf () {
        return new CatLong(get());
    }
}
