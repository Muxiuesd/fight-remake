package ttk.muxiuesd.data;

import com.badlogic.gdx.utils.Json;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.data.abs.JsonDataOutput;
import ttk.muxiuesd.util.FileUtil;
import ttk.muxiuesd.world.WorldInfo;

public class WorldInfoDataOutput extends JsonDataOutput {
    @Override
    public void output (JsonDataWriter writer) {
        Json json = writer.getWriter();
        String string = json.getWriter().getWriter().toString();
        FileUtil.deleteFile(Fight.PATH_SAVE, WorldInfo.FILE_NAME);
        FileUtil.createFile(Fight.PATH_SAVE, WorldInfo.FILE_NAME).writeString(json.prettyPrint(string), false);
    }
}
