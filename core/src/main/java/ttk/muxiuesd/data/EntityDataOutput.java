package ttk.muxiuesd.data;

import com.badlogic.gdx.utils.Json;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import game.muxiuesd.bedrockcore.util.UnifiedFileUtil;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.abs.JsonDataOutput;

/**
 * 实体数据输出
 * */
public class EntityDataOutput extends JsonDataOutput {
    private final String fileName;    //不包含后缀

    public EntityDataOutput (String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void output (JsonDataWriter writer) {
        Json json = writer.getWriter();
        String string = json.getWriter().getWriter().toString();
        /*AbsFileUtil.createFile(Fight.getPathSaveEntities(), this.fileName + ".json")
            .writeString(json.prettyPrint(string), false);*/

        UnifiedFileUtil
            .createFile(Fight.getPathSaveEntities(), this.fileName + ".json")
            .writeString(json.prettyPrint(string), false);
    }
}
