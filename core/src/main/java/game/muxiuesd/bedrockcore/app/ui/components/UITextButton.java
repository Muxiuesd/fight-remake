package game.muxiuesd.bedrockcore.app.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import ttk.muxiuesd.registry.Fonts;
import ttk.muxiuesd.ui.text.FontHolder;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.util.TextUtil;

/**
 * 带有文本字体渲染的按钮，字体默认渲染在按钮的中心位置
 * */
public class UITextButton extends UIButton {

    public static final ClickEvent VOID_CLICK_EVENT = (button, interactPos) -> {
        //啥也不做
        return false;
    };

    private Text text;
    private FontHolder fontHolder;

    public UITextButton () {
        this(Text.NULL_TEXT, VOID_CLICK_EVENT);
    }
    public UITextButton (Text text, ClickEvent clickEvent) {
        this(text, Fonts.MC, clickEvent);
    }
    public UITextButton (Text text, FontHolder fontHolder, ClickEvent clickEvent) {
        super(clickEvent);
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
