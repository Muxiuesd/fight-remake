package game.muxiuesd.bedrockcore.util;

import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * 跟贴图材质文件相关的工具
 * */
public class TextureUtil {
    /**
     * 创建点九
     * */
    public static NinePatch createNinePatch(TextureRegion textureRegion, int left, int right, int top, int bottom) {
        return new NinePatch(textureRegion, left, right, top, bottom);
    }
}
