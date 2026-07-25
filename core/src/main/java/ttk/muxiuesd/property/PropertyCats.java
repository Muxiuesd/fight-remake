package ttk.muxiuesd.property;

import game.muxiuesd.bedrockcore.app.interfaces.data.DataReader;
import game.muxiuesd.bedrockcore.app.interfaces.data.DataWriter;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import game.muxiuesd.bedrockcore.serialization.Codec;
import ttk.muxiuesd.world.cat.CatsHolder;

/**
 * 自定义属性标签
 * */
public class PropertyCats extends PropertyType<CatsHolder>{
    @Override
    public void write (DataWriter<?> writer, CatsHolder holder) {
        if (writer instanceof JsonDataWriter jsonWriter) {
            jsonWriter.objStart(getId());
            //对每一个cat值调用他们的写入方法
            holder.getMap().forEach((key, value) -> {
                value.write(key, jsonWriter);
            });
            jsonWriter.objEnd();
        }
    }

    @Override
    public CatsHolder read (DataReader<?> reader, String dataKey) {
        return new CatsHolder();
    }

    @Override
    public Codec<CatsHolder> getValueCodec () {
        return null;
    }
}
