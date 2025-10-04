package ttk.muxiuesd.ui.text;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ObjectMap;
import ttk.muxiuesd.util.Log;

/**
 * 游戏中的字体持有者
 * */
public class FontHolder {
    private final String id;
    private final FreeTypeFontGenerator generator;

    private final ObjectMap<Integer, BitmapFont> fontsCache;    //不同字号的字体缓存

    public FontHolder (String id, FreeTypeFontGenerator generator) {
        this.id = id;
        this.generator = generator;
        this.fontsCache = new ObjectMap<>();
    }

    /**
     * 获取字体
     * @param size 字体大小
     * */
    public BitmapFont getFont(int size) {
        //查找缓存，有的这个字号的缓存就直接获取
        if (this.fontsCache.containsKey(size)) {
            return this.fontsCache.get(size);
        }

        if (this.generator == null) {
            Log.error(this.getClass().getName(), "无法获取id为：" + id + " 的字体！！！");
            return null;
        }

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        //parameter.characters = this.fullCharacters();
        parameter.genMipMaps = false;
        parameter.magFilter = Texture.TextureFilter.Nearest;
        parameter.minFilter = Texture.TextureFilter.Nearest;
        parameter.incremental = true;
        parameter.size = size;
        // 可以设置其他参数，如颜色、边框等

        //根据字号生成
        BitmapFont font = this.generator.generateFont(parameter);
        this.fontsCache.put(size, font);
        return font;
    }

    private String fullCharacters () {
        return "abcdefghijklmnopqrstuvwxyz" +
        "0123456789" +
        ",./;'[]-=<>?:\"\\|_+!@#$%^&*(){}" +
        "，。/；‘【】、！￥……《》？：“”、（）" +
        ChineseCharacters.CHARACTERS;
    }
}
