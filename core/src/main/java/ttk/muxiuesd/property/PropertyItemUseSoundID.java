package ttk.muxiuesd.property;

import ttk.muxiuesd.interfaces.data.DataWriter;

public class PropertyItemUseSoundID extends PropertyType<String> {
    @Override
    public void write (DataWriter<?> writer, String data) {
        writer.writeString(getName(), data);
    }
}
