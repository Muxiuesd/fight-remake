package game.muxiuesd.bedrockcore.app.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import game.muxiuesd.bedrockcore.app.interfaces.Voidable;
import game.muxiuesd.bedrockcore.app.ui.abs.UIComponent;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.util.Util;

/**
 * 按钮组件，没有字体文本渲染
 * */
public class UIButton extends UIComponent {
    public static final int DEFAULT_EDGE = 3;
    public static final float DEFAULT_WIDTH = 80f;
    public static final float DEFAULT_HEIGHT = 14f;


    private NinePatch backgroundPatch;
    private NinePatch clickBackgroundPatch;
    private NinePatch mouseOverBackgroundPatch;
    private Click click;
    private MouseOver mouseOver;


    public UIButton(Click click) {
        this(click, (button, interactPos) -> {});
    }

    public UIButton(Click click, MouseOver mouseOver) {
        this(
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
            mouseOver
        );
    }
    public UIButton(TextureRegion background, TextureRegion clickBackground, TextureRegion mouseOverBackground,
                    Click click, MouseOver mouseOver) {
        this(
            createNinePatch(background, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE),
            createNinePatch(clickBackground, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE),
            createNinePatch(mouseOverBackground, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE),
            DEFAULT_WIDTH, DEFAULT_HEIGHT, new GridPoint2(DEFAULT_EDGE, DEFAULT_EDGE),
            click, mouseOver
        );
    }

    public UIButton (NinePatch backgroundPatch, NinePatch clickBackground, NinePatch mouseOverBackground,
                     float width, float height, GridPoint2 interactSize,
                     Click click, MouseOver mouseOver) {
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

    /**
     * 点击按钮事件处理接口
     * */
    public interface Click extends Voidable {
        boolean handle (UIButton button, GridPoint2 interactPos);
    }

    /**
     * 鼠标放在按钮上面的事件处理接口
     * */
    public interface MouseOver {
        void handle (UIButton button, GridPoint2 interactPos);
    }
}
