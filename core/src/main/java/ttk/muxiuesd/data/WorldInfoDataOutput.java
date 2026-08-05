package ttk.muxiuesd.data;

import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import game.muxiuesd.bedrockcore.util.UnifiedFileUtil;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.abs.JsonDataOutput;
import ttk.muxiuesd.world.WorldInfo;

/**
 * 世界信息数据输出
 * */
public class WorldInfoDataOutput extends JsonDataOutput {
    @Override
    public void output (JsonDataWriter writer) {
        //旧式 JsonDataWriter 输出已弃用，新式路径使用 output(String)
    }

    /**
     * 输出世界信息（新式：直接写入 RawObject 序列化后的 JSON 字符串）
     * */
    public void output (String json) {
        //原子写入：防止写盘被中断导致半截文件损坏存档
        UnifiedFileUtil.writeFileAtomic(Fight.getPathSaveWorld(), WorldInfo.FILE_NAME, json);
    }
}
