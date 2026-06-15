package ttk.muxiuesd.resource;

import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import game.muxiuesd.bedrockcore.util.UnifiedFileUtil;

/**
 * 游戏资源加载器的文件处理解析，用于识别要加载内部文件还是外部文件，使用统一文件工具
 * */
public class FightFileHandleResolver implements FileHandleResolver {
    @Override
    public FileHandle resolve (String fileName) {
        if (fileName.startsWith(UnifiedFileUtil.INTERNAL_MARK)) {
            //一定要处理标记
            String path = UnifiedFileUtil.removePathStarts(fileName);
            return UnifiedFileUtil.INTERNAL_FILE_UTIL.getFileHandle(path);
        }
        if (fileName.startsWith(UnifiedFileUtil.ABSOLUTE_MARK)) {
            String path = UnifiedFileUtil.removePathStarts(fileName);
            return UnifiedFileUtil.ABSOLUTE_FILE_UTIL.getFileHandle(path);
        }
        if (fileName.startsWith(UnifiedFileUtil.EXTERNAL_MARK)) {
            String path = UnifiedFileUtil.removePathStarts(fileName);
            return UnifiedFileUtil.EXTERNAL_FILE_UTIL.getFileHandle(path);
        }
        //没有对应的开头就默认是游戏内部文件
        return UnifiedFileUtil.INTERNAL_FILE_UTIL.getFileHandle(fileName);
    }
}
