package ttk.muxiuesd.data;

import com.badlogic.gdx.utils.Json;
import game.muxiuesd.bedrockcore.data.JsonDataWriter;
import game.muxiuesd.bedrockcore.util.UnifiedFileUtil;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.abs.JsonDataOutput;
import ttk.muxiuesd.system.PlayerSystem;

/**
 * 玩家数据输出类
 * */
public class PlayerDataOutput extends JsonDataOutput {
    @Override
    public void output (JsonDataWriter writer) {
        Json json = writer.getWriter();
        String string = json.getWriter().getWriter().toString();
        /*AbsFileUtil.createFile(Fight.getPathSavePlayer(), PlayerSystem.PLAYER_DATA_FILE_NAME)
            .writeString(json.prettyPrint(string), false);*/

        UnifiedFileUtil
            .createFile(Fight.getPathSavePlayer(), PlayerSystem.PLAYER_DATA_FILE_NAME)
            .writeString(json.prettyPrint(string), false);
    }
}
