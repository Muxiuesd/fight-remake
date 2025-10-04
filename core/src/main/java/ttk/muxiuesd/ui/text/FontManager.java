package ttk.muxiuesd.ui.text;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ObjectMap;
import ttk.muxiuesd.util.Log;

/**
 * 字体管理器
 */
public class FontManager {
    public static final String TAG = FontManager.class.getName();

    public static final ObjectMap<String, FontHolder> FONT_HOLDERS = new ObjectMap<>();

    /**
     * 注册一种字体
     * */
    public static FontHolder registerFont(String id, FileHandle fontFile) {
        FontHolder fontHolder = new FontHolder(id, loadGenerator(fontFile));
        FONT_HOLDERS.put(id, fontHolder);
        return fontHolder;
    }

    /**
     * 加载字体生成器
     * */
    public static FreeTypeFontGenerator loadGenerator (FileHandle fontFile) {
        FreeTypeFontGenerator generator;
        try {
            generator = new FreeTypeFontGenerator(fontFile);
        } catch (Exception e) {
            Log.error(TAG, "无法加载字体：" + fontFile.path());
            throw e;
        }
        return generator;
    }

}
