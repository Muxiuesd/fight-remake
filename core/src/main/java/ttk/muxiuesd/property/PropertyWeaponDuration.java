package ttk.muxiuesd.property;

import ttk.muxiuesd.interfaces.data.DataWriter;
/**
 * 武器基础耐久
 * */
public class PropertyWeaponDuration extends PropertyType<Integer> {
    @Override
    public void write (DataWriter<?> writer, Integer data) {
        writer.writeInt(getName(), data);
    }
}
