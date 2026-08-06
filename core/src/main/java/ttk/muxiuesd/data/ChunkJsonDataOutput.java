package ttk.muxiuesd.data;

import com.badlogic.gdx.utils.Json;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import game.muxiuesd.bedrockcore.util.UnifiedFileUtil;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.abs.JsonDataOutput;

/**
 * 区块的json数据输出
 * */
public class ChunkJsonDataOutput extends JsonDataOutput {
    private String fileName;    //不包含后缀

    public ChunkJsonDataOutput (String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void output (JsonDataWriter writer) {
        Json json = writer.getWriter();
        String string = json.getWriter().getWriter().toString();
        UnifiedFileUtil.createFile(Fight.getPathSaveChunks(), this.fileName + ".json")
            .writeString(json.prettyPrint(string), false);
    }
}


