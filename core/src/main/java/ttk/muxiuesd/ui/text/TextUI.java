package ttk.muxiuesd.ui.text;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import ttk.muxiuesd.registry.Fonts;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.ui.components.UIPanel;

/**
 * 文本UI组件
 * */
public class TextUI extends UIComponent {
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
        this.text = text;
        this.fontHolder = fontHolder;
        this.fontSize = fontSize;
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        BitmapFont font = this.getFontHolder().getFont(this.getFontSize());
        font.draw(batch, this.getText(), getX(), getY() + this.getFontSize());
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
}
