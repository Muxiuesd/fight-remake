package ttk.muxiuesd.registry;

import com.badlogic.gdx.Gdx;
import game.muxiuesd.bedrockcore.font.FontHolder;
import game.muxiuesd.bedrockcore.font.FontManager;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;

/**
 * 字体注册
 * */
public class Fonts {
    public static void init() {}

    public static FontHolder MC = register("mc_font");


    public static FontHolder register (String name) {
        return FontManager.registerFont(Identifier.of(Fight.ID(name)), Gdx.files.internal(Fight.FontPath(name)));
    }
}
