package ttk.muxiuesd.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import ttk.muxiuesd.interfaces.data.IDataOutput;
import ttk.muxiuesd.registry.PropertyTypes;

/**
 * json格式的数据的输出实现类
 * */
public class JsonDataOutput implements IDataOutput<JsonDataWriter> {
    @Override
    public void output (JsonDataWriter writer) {
        //输出测试
        Json json = writer.getWriter();
        String string = json.getWriter().getWriter().toString();
        Gdx.app.log("JsonDataOutput", string);

        String storagePath = Gdx.files.getLocalStoragePath();
        System.out.println(storagePath);
        String savePath = storagePath + "/save";
        FileHandle fileHandle = Gdx.files.absolute(savePath);
        if (!fileHandle.exists()) {
            //新建文件夹
            fileHandle.mkdirs();
        }
        Gdx.files.absolute(savePath + "/item.json").writeString(string, false);

        //读取测试
        String s = Gdx.files.absolute(savePath + "/item.json").readString();
        System.out.println(s);
        JsonDataReader dataReader = new JsonDataReader(s);
        String read = dataReader.readString(PropertyTypes.ITEM_USE_SOUND_ID.getName());
        System.out.println(read);
    }
}
