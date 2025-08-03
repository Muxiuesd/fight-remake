package ttk.muxiuesd.data;

import com.badlogic.gdx.utils.Json;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.abs.JsonDataOutput;
import ttk.muxiuesd.system.PlayerSystem;
import ttk.muxiuesd.util.FileUtil;

/**
 * 玩家数据输出类
 * */
public class PlayerDataOutput extends JsonDataOutput {
    @Override
    public void output (JsonDataWriter writer) {
        Json json = writer.getWriter();
        String string = json.getWriter().getWriter().toString();
        FileUtil.createFile(Fight.PATH_SAVE_PLAYER, PlayerSystem.PLAYER_DATA_FILE_NAME)
            .writeString(json.prettyPrint(string), false);
    }
}
