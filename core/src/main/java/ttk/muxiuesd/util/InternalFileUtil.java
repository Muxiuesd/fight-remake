package ttk.muxiuesd.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import game.muxiuesd.bedrockcore.util.FileUtil;

/**
 * 游戏jar内部文件的文件调用工具
 * <p>
 * 规定：path都是基于游戏jar内部文件的路径（内部基准路径，即assets/）起始的路径，也就是说这个文件工具是内部文件工具
 * */
public class InternalFileUtil extends FileUtil {
    public static InternalFileUtil INSTANCE = new InternalFileUtil();
    public static InternalFileUtil getInstance() {
        return INSTANCE;
    }
    private InternalFileUtil () {}


    @Override
    public FileHandle getFileHandle (String path, String name) {
        return this.getFileHandle(path + "/" + name);
    }

    @Override
    public FileHandle getFileHandle (String path) {
        return Gdx.files.internal(path);
    }

    @Override
    public String getRootPath () {
        return "";
    }
}
