package ttk.muxiuesd.registry;

import com.badlogic.gdx.Gdx;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.ui.text.FontHolder;
import ttk.muxiuesd.ui.text.FontManager;

/**
 * 字体注册
 * */
public class Fonts {
    public static void init() {}

    public static FontHolder MC = register("mc_font");


    public static FontHolder register (String name) {

        String id = Fight.ID(name);
        return FontManager.registerFont(id, Gdx.files.internal(Fight.FontPath(name)));
    }
}
