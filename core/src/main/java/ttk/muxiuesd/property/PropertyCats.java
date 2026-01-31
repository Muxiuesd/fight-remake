package ttk.muxiuesd.property;

import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.interfaces.data.DataReader;
import ttk.muxiuesd.interfaces.data.DataWriter;
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
}
