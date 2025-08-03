package ttk.muxiuesd.data;

import com.badlogic.gdx.utils.Json;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.abs.JsonDataOutput;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.util.FileUtil;

/**
 * 物品的json输出类
 * */
public class ItemJsonDataOutput extends JsonDataOutput {
    @Override
    public void output (JsonDataWriter writer) {
        //输出测试
        Json json = writer.getWriter();
        String string = json.getWriter().getWriter().toString();

        FileUtil.createFile(Fight.PATH_SAVE, "item.json").writeString(json.prettyPrint(string), false);

        //读取测试
        String s = FileUtil.readFileAsString(Fight.PATH_SAVE, "item.json");
        System.out.println(s);
        JsonDataReader dataReader = new JsonDataReader(s);
        String read = dataReader.readString(PropertyTypes.ITEM_USE_SOUND_ID.getId());
        System.out.println(read);

    }
}
