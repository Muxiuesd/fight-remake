package ttk.muxiuesd.data;

import com.badlogic.gdx.utils.Json;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.abs.JsonDataOutput;
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
    }
}
