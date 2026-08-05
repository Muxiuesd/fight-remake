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
        //原子写入：先写临时文件再重命名，防止写盘被中断导致半截文件损坏存档
        UnifiedFileUtil.writeFileAtomic(Fight.getPathSaveEntities(), this.fileName + ".json", json.prettyPrint(string));
    }
}
