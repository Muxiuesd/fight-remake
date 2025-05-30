package ttk.muxiuesd.property;

import ttk.muxiuesd.interfaces.data.DataWriter;

public class DefaultIntPropertyType extends PropertyType<Integer>{
    @Override
    public void write (DataWriter<?> writer, Integer data) {
        writer.writeInt(getName(), data);
    }
}
