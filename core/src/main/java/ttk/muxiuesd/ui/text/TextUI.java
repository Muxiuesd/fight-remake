package ttk.muxiuesd.ui.text;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.ui.abs.UIComponent;
import ttk.muxiuesd.registry.Fonts;
import ttk.muxiuesd.ui.components.UIPanel;
import ttk.muxiuesd.util.TextUtil;

/**
 * 文本UI组件
 * */
public class TextUI extends UIComponent {
    private GlyphLayout glyphLayout;
    private String text;
    private FontHolder fontHolder;
    private int fontSize;

    public TextUI () {
        this("Null");
    }
    public TextUI (String text) {
        this(Fonts.MC, text, 7);
    }
    public TextUI (FontHolder fontHolder, String text, int fontSize) {
        this.glyphLayout = new GlyphLayout();
        this.text = text;
        this.fontHolder = fontHolder;
        this.fontSize = fontSize;
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        BitmapFont font = this.getFontHolder().getFont(this.getFontSize());
        TextUtil.draw(batch, font, this.getText(), getX(), getY() + this.getFontSize());
    }

    /**
     * 获取整个文本渲染出来的宽度
     * */
    public float getRenderWidth () {
        this.glyphLayout.setText(this.getFontHolder().getFont(this.getFontSize()), this.getText());
        return glyphLayout.width;
    }

    public GlyphLayout getGlyphLayout () {
        return this.glyphLayout;
    }

    public TextUI setGlyphLayout (GlyphLayout glyphLayout) {
        this.glyphLayout = glyphLayout;
        return this;
    }

    public String getText () {
        return this.text;
    }

    public TextUI setText (String text) {
        this.text = text;
        return this;
    }

    public FontHolder getFontHolder () {
        return this.fontHolder;
    }

    public TextUI setFontHolder (FontHolder fontHolder) {
        this.fontHolder = fontHolder;
        return this;
    }

    public int getFontSize () {
        return this.fontSize;
    }

    public TextUI setFontSize (int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    @Override
    public TextUI setPosition (float x, float y) {
        super.setPosition(x, y);
        return this;
    }

    @Override
    public TextUI setPosition (Vector2 pos) {
        super.setPosition(pos);
        return this;
    }
}
