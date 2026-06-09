package ttk.muxiuesd.data;

import com.badlogic.gdx.utils.Json;
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
        Json json = writer.getWriter();
        String string = json.getWriter().getWriter().toString();
        //FileUtil.deleteFile(Fight.PATH_SAVE, WorldInfo.FILE_NAME);
        /*AbsFileUtil.createFile(Fight.getPathSaveWorld(), WorldInfo.FILE_NAME)
            .writeString(json.prettyPrint(string), false);*/

        UnifiedFileUtil
            .createFile(Fight.getPathSaveWorld(), WorldInfo.FILE_NAME)
            .writeString(json.prettyPrint(string), false);
    }
}
