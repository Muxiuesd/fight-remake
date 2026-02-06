package ttk.muxiuesd.property;

import game.muxiuesd.bedrockcore.app.interfaces.data.DataReader;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataWriter;

/**
 * 默认的布尔属性类型
 * */
public class DefaultBoolPropertyType extends PropertyType<Boolean>{
    @Override
    public void write (DataWriter<?> writer, Boolean data) {
        writer.writeBoolean(getId(), data);
    }

    @Override
    public Boolean read (DataReader<?> reader, String dataKey) {
        return reader.readBoolean(dataKey);
    }
}
