package ttk.muxiuesd.data;

import com.badlogic.gdx.utils.Json;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.abs.JsonDataOutput;
import ttk.muxiuesd.util.FileUtil;

/**
 * 实体背包数据的输出类
 * */
public class BackpackDataOutput extends JsonDataOutput {
    private String fileName;    //不包含后缀

    public BackpackDataOutput (String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void output (JsonDataWriter writer) {
        Json json = writer.getWriter();
        String string = json.getWriter().getWriter().toString();
        FileUtil.createFile(Fight.PATH_SAVE_ENTITIES, this.fileName + ".json")
            .writeString(json.prettyPrint(string), false);
    }
}
