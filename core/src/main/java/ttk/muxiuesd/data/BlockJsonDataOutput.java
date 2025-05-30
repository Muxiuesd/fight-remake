package ttk.muxiuesd.data;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.abs.JsonDataOutput;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.util.FileUtil;

/**
 * 方块的json输出类
 * */
public class BlockJsonDataOutput extends JsonDataOutput {
    @Override
    public void output (JsonDataWriter writer) {
        Json json = writer.getWriter();
        String string = json.getWriter().getWriter().toString();
        FileUtil.createFile(Fight.PATH_SAVE, "block.json").writeString(json.prettyPrint(string), false);

        //读取测试
        String s = FileUtil.readFileAsString(Fight.PATH_SAVE, "block.json");
        System.out.println(s);
        JsonDataReader dataReader = new JsonDataReader(s);
        JsonValue obj = dataReader.readObj(PropertyTypes.BLOCK_SOUNDS_ID.getName());
        System.out.println(obj.toJson(JsonWriter.OutputType.json));
    }
}
