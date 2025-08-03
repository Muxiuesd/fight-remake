package ttk.muxiuesd.property;

import ttk.muxiuesd.interfaces.data.DataReader;
import ttk.muxiuesd.interfaces.data.DataWriter;
import ttk.muxiuesd.world.entity.abs.Entity;

public class PropertyItemWithEntity extends PropertyType<Entity<?>>{
    @Override
    public void write (DataWriter<?> writer, Entity<?> data) {
        //TODO
    }

    @Override
    public Entity <?>read (DataReader<?> reader, String dataKey) {
        //TODO
        return null;
    }
}
