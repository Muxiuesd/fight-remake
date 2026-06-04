package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import game.muxiuesd.bedrockcore.app.ui.components.UITextField;
import game.muxiuesd.bedrockcore.font.FontHolder;
import game.muxiuesd.bedrockcore.util.TextureUtil;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.util.Util;

/**
 * 游戏内具体实现的文本框UI组件
 * */
public class FightUITextField extends UITextField {

    /**
     * 默认的构造方法
     * */
    public FightUITextField (float width, float height, FontHolder fontHolder) {
        this(
            0, 0, width, height, fontHolder,
            Util.loadTextureRegion(
                Fight.ID("text_field_background"),
                Fight.UITexturePath("text_field_background.png")
            ),
            Util.loadTextureRegion(
                Fight.ID("cursor"),
                Fight.UITexturePath("cursor.png")
            )
        );
    }
    public FightUITextField (float x, float y,
                             float width, float height,
                             FontHolder fontHolder,
                             TextureRegion backgroundTexture, TextureRegion cursorTexture) {
        super(
            x, y, width, height, fontHolder,
            TextureUtil.createNinePatch(backgroundTexture, 2, 2, 2, 2),
            TextureUtil.createNinePatch(cursorTexture, 1, 1, 2, 2)
        );
    }
}
