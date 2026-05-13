package ttk.muxiuesd.property;

import game.muxiuesd.bedrockcore.app.interfaces.data.DataReader;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataWriter;

public class PropertyFishingPoleUsing extends DefaultBoolPropertyType{
    @Override
    public void write (DataWriter<?> writer, Boolean data) {
        writer.writeBoolean(getId(), false);
    }

    @Override
    public Boolean read (DataReader<?> reader, String dataKey) {
        return false;
    }
}
