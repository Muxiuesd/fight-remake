package ttk.muxiuesd.property;

import ttk.muxiuesd.interfaces.data.DataWriter;
/**
 * 武器基础伤害
 * */
public class PropertyWeaponDamage extends PropertyType<Float> {
    @Override
    public void write (DataWriter<?> writer, Float data) {
        writer.writeFloat(getName(), data);
    }
}
