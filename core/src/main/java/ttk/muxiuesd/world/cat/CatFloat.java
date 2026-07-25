package ttk.muxiuesd.world.cat;

import com.badlogic.gdx.utils.JsonValue;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;

public class CatFloat extends CatValue<Float>{
    public CatFloat (float value) {
        super(value);
    }

    @Override
    public void write (String key, JsonDataWriter writer) {
        writer.writeFloat(key, get());
    }

    @Override
    public void read (String key, JsonValue values) {
        set(values.getFloat(key));
    }

    @Override
    public CatValue<Float> newSelf () {
        return new CatFloat(get());
    }
}
