package game.muxiuesd.bedrockcore.app.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import game.muxiuesd.bedrockcore.font.FontHolder;
import game.muxiuesd.bedrockcore.util.TextureUtil;
import ttk.muxiuesd.registry.Fonts;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.util.TextUtil;

/**
 * 带有文本字体渲染的按钮，字体默认渲染在按钮的中心位置
 * */
public class UITextButton extends UIButton {
    private Text text;
    private FontHolder fontHolder;


    public UITextButton (TextureRegion background, TextureRegion clickBackground, TextureRegion mouseOverBackground,
                         Text text, ClickEvent clickEvent, MouseOverEvent mouseOverEvent) {
        this(
            TextureUtil.createNinePatch(background, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE),
            TextureUtil.createNinePatch(clickBackground, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE),
            TextureUtil.createNinePatch(mouseOverBackground, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE),
            DEFAULT_WIDTH, DEFAULT_HEIGHT, new GridPoint2((int) DEFAULT_WIDTH, (int) DEFAULT_HEIGHT),
            text, Fonts.MC, clickEvent, mouseOverEvent
        );
    }
    public UITextButton (TextureRegion background, TextureRegion clickBackground, TextureRegion mouseOverBackground,
                         float width, float height, GridPoint2 interactSize,
                         Text text, ClickEvent clickEvent, MouseOverEvent mouseOverEvent) {
        this(
            TextureUtil.createNinePatch(background, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE),
            TextureUtil.createNinePatch(clickBackground, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE),
            TextureUtil.createNinePatch(mouseOverBackground, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE),
            width, height, interactSize, text, Fonts.MC, clickEvent, mouseOverEvent
        );
    }
    public UITextButton (NinePatch backgroundPatch, NinePatch clickBackground, NinePatch mouseOverBackground,
                         float width, float height, GridPoint2 interactSize,
                         Text text, FontHolder fontHolder,
                         ClickEvent clickEvent, MouseOverEvent mouseOverEvent) {
        super(backgroundPatch, clickBackground, mouseOverBackground, width, height, interactSize, clickEvent, mouseOverEvent);
        this.text = text;
        this.fontHolder = fontHolder;
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        super.draw(batch, parent);

        float x = getX(parent);
        float y = getY(parent);
        //渲染字体
        if (this.text != null) {
            BitmapFont bitmapFont = this.getFontHolder().getFont(FontHolder.FONT_SIZE);
            bitmapFont.getData().setScale(FontHolder.FONT_SCALE);

            //让文本在按钮中央渲染
            int renderSize = this.getFontRenderSize(FontHolder.FONT_SIZE, FontHolder.FONT_SCALE);
            float renderWidth = TextUtil.getTextRenderWidth(bitmapFont, this.getText().getString());
            float renderX = x + (getWidth() - renderWidth) / 2;

            //font.draw(batch, this.getText(), renderX, y + renderSize + DEFAULT_EDGE);

            TextUtil.draw(batch, bitmapFont, this.getText().getString(), renderX, y + renderSize + DEFAULT_EDGE);

            //恢复共享字体的缩放，防止影响其他使用同一字体的组件
            bitmapFont.getData().setScale(1f);
        }
    }

    public Text getText () {
        return this.text;
    }

    public UITextButton setText (Text text) {
        this.text = text;
        return this;
    }

    public FontHolder getFontHolder () {
        return this.fontHolder;
    }

    public UITextButton setFontHolder (FontHolder fontHolder) {
        this.fontHolder = fontHolder;
        return this;
    }

    public int getFontRenderSize (int fontSize, float fontScale) {
        return (int) (fontSize * fontScale);
    }
}
