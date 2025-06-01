package ttk.muxiuesd.property;


import ttk.muxiuesd.data.JsonDataWriter;
import ttk.muxiuesd.interfaces.data.DataWriter;
import ttk.muxiuesd.world.cat.CAT;

/**
 * 属性：自定义属性标签
 * */
public class PropertyCAT extends PropertyType<CAT>{
    @Override
    public void write (DataWriter<?> writer, CAT data) {
        if (writer instanceof JsonDataWriter jsonWriter) {
            jsonWriter.objStart(getName());
            data.write(jsonWriter);
            jsonWriter.objEnd();
        }
    }
}
