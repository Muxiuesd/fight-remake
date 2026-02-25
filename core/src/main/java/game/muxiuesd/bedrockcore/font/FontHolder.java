package game.muxiuesd.bedrockcore.font;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.ObjectMap;
import game.muxiuesd.bedrockcore.util.Log;

/**
 * 游戏中的字体持有者
 * */
public class FontHolder {
    public static final int FONT_SIZE = 16; //字体大小，最好是8的整数倍，不然中文字体会糊
    public static final float FONT_SCALE = 0.5f; //字体缩放，最好缩放后也是8的整数倍

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
    public BitmapFont getFont (int size) {
        //查找缓存，有的这个字号的缓存就直接获取
        if (this.fontsCache.containsKey(size)) {
            return this.fontsCache.get(size);
        }

        if (this.generator == null) {
            Log.error(this.getClass().getName(), "无法获取id为：" + id + " 的字体！！！");
            return null;
        }

        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.genMipMaps = false;
        parameter.magFilter = Texture.TextureFilter.Nearest;
        parameter.minFilter = Texture.TextureFilter.Nearest;
        parameter.incremental = true;
        parameter.size = size;

        // 设置阴影
        parameter.shadowOffsetX = 1; // 阴影横向偏移
        parameter.shadowOffsetY = 1; // 阴影纵向偏移
        parameter.shadowColor = new Color(0, 0, 0, 0.50f); // 阴影颜色（黑色，75%不透明度）

        //根据字号生成
        BitmapFont font = this.generator.generateFont(parameter);
        this.fontsCache.put(size, font);
        return font;
    }
}
