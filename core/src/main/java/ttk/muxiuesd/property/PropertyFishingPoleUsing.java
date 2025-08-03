package ttk.muxiuesd.property;

import ttk.muxiuesd.interfaces.data.DataReader;
import ttk.muxiuesd.interfaces.data.DataWriter;

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
