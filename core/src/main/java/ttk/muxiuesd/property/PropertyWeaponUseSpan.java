package ttk.muxiuesd.property;

import ttk.muxiuesd.interfaces.data.DataWriter;

public class PropertyWeaponUseSpan extends PropertyType<Float>{
    @Override
    public void write (DataWriter<?> writer, Float data) {
        writer.writeFloat(getName(), data);
    }
}
