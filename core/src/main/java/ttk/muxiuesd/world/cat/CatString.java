package ttk.muxiuesd.world.cat;

import com.badlogic.gdx.utils.JsonValue;
import ttk.muxiuesd.data.JsonDataWriter;

public class CatString extends CatValue<String> {

    public CatString (String value) {
        super(value == null ? "NullCatString": value);
    }

    @Override
    public void write (String key, JsonDataWriter writer) {
        writer.writeString(key, this.get());
    }

    @Override
    public void read (String key, JsonValue values) {
        set(values.getString(key));
    }

    @Override
    public CatValue<String> newSelf () {
        return new CatString(get());
    }
}
