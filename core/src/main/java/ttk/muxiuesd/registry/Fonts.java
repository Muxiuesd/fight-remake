package ttk.muxiuesd.registry;

import com.badlogic.gdx.Gdx;
import game.muxiuesd.bedrockcore.font.FontHolder;
import game.muxiuesd.bedrockcore.font.FontManager;
import ttk.muxiuesd.Fight;

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
