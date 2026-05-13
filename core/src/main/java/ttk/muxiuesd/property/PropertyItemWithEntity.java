package ttk.muxiuesd.property;

import game.muxiuesd.bedrockcore.app.interfaces.data.DataReader;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataWriter;
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
