package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.registry.Fonts;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.ui.text.FontHolder;
import ttk.muxiuesd.util.Util;

/**
 * 按钮组件
 * */
public class UIButton extends UIComponent {
    public static final int DEFAULT_EDGE = 3;
    public static final float DEFAULT_WIDTH = 80f;
    public static final float DEFAULT_HEIGHT = 14f;

    private String text;
    private FontHolder fontHolder;
    private NinePatch backgroundPatch;
    private NinePatch clickBackgroundPatch;
    private NinePatch mouseOverBackgroundPatch;
    private Click click;
    private MouseOver mouseOver;

    public UIButton(String text, Click click) {
        this(
            text, Fonts.MC,
            Util.loadTextureRegion(
                Fight.ID("button"),
                Fight.UITexturePath("button.png")
            ),
            Util.loadTextureRegion(
                Fight.ID("button_clicked"),
                Fight.UITexturePath("button_clicked.png")
            ),
            Util.loadTextureRegion(
                Fight.ID("button_mouse_over"),
                Fight.UITexturePath("button_mouse_over.png")
            ),
            click,
            (button, interactPos) -> {}
        );
    }
    public UIButton(String text, FontHolder fontHolder,
                    TextureRegion background, TextureRegion clickBackground, TextureRegion mouseOverBackground,
                    Click click, MouseOver mouseOver) {
        this(
            text, fontHolder,
            new NinePatch(background, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE),
            new NinePatch(clickBackground, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE),
            new NinePatch(mouseOverBackground, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE),
            DEFAULT_WIDTH, DEFAULT_HEIGHT, new GridPoint2(DEFAULT_EDGE, DEFAULT_EDGE),
            click, mouseOver
        );
    }

    public UIButton (String text, FontHolder fontHolder,
                     NinePatch backgroundPatch, NinePatch clickBackground, NinePatch mouseOverBackground,
                     float width, float height, GridPoint2 interactSize,
                     Click click, MouseOver mouseOver) {
        this.text = text;
        this.fontHolder = fontHolder;
        this.backgroundPatch = backgroundPatch;
        this.clickBackgroundPatch = clickBackground;
        this.mouseOverBackgroundPatch = mouseOverBackground;
        this.click = click;
        this.mouseOver = mouseOver;

        setSize(width, height);
        setInteractGridSize(interactSize);
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        float x = getX(parent);
        float y = getY(parent);
        if (isClicked() && this.clickBackgroundPatch != null) {
            this.clickBackgroundPatch.draw(batch, x, y, getWidth(), getHeight());
        }else if (isMouseOver() && this.mouseOverBackgroundPatch != null) {
            this.mouseOverBackgroundPatch.draw(batch, x, y, getWidth(), getHeight());
        }else if (this.backgroundPatch != null) {
            this.backgroundPatch.draw(batch, x, y, getWidth(), getHeight());
        }

        //渲染字体
        if (this.text != null) {
            BitmapFont font = this.getFontHolder().getFont(FontHolder.FONT_SIZE);
            font.getData().setScale(FontHolder.FONT_SCALE);
            //让文本在按钮中央渲染
            int renderSize = this.getFontRenderSize(FontHolder.FONT_SIZE, FontHolder.FONT_SCALE);
            float renderX = x + (getWidth() - renderSize * this.getText().length()) / 2;
            font.draw(batch, this.getText(), renderX, y + renderSize + DEFAULT_EDGE);
        }
    }

    @Override
    public boolean click (GridPoint2 interactPos) {
        AudioPlayer.getInstance().playMusic(Sounds.ITEM_CLICK);

        if (this.click == null) return super.click(interactPos);

        return this.click.handle(this, interactPos);
    }

    @Override
    public void mouseOver (GridPoint2 interactPos) {
        if (this.mouseOver == null) super.mouseOver(interactPos);
        this.mouseOver.handle(this, interactPos);
    }

    public String getText () {
        return this.text;
    }

    public UIButton setText (String text) {
        this.text = text;
        return this;
    }

    public FontHolder getFontHolder () {
        return this.fontHolder;
    }

    public UIButton setFontHolder (FontHolder fontHolder) {
        this.fontHolder = fontHolder;
        return this;
    }

    public int getFontRenderSize (int fontSize, float fontScale) {
        return (int) (fontSize * fontScale);
    }

    /**
     * 点击按钮事件处理接口
     * */
    public interface Click {
        boolean handle (UIButton button, GridPoint2 interactPos);
    }

    /**
     * 点击按钮事件处理接口
     * */
    public interface MouseOver {
        void handle (UIButton button, GridPoint2 interactPos);
    }
}
