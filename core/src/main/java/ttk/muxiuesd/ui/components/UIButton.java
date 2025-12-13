package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.audio.AudioPlayer;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.util.Util;

/**
 * 按钮组件
 * */
public class UIButton extends UIComponent {
    public static final int DEFAULT_EDGE = 3;
    public static final float DEFAULT_WIDTH = 30f;
    public static final float DEFAULT_HEIGHT = 8f;

    private Click click;
    private NinePatch backgroundPatch;

    public UIButton(Click click) {
        this(
            Util.loadTextureRegion(
                Fight.ID("button"),
                Fight.UITexturePath("tooltip_background.png")
            ),
            click
        );
    }
    public UIButton(TextureRegion background, Click click) {
        this(new NinePatch(background, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE),
            DEFAULT_WIDTH, DEFAULT_HEIGHT, new GridPoint2(DEFAULT_EDGE, DEFAULT_EDGE),
            click);
    }

    public UIButton (NinePatch backgroundPatch, float width, float height, GridPoint2 interactSize, Click click) {
        this.backgroundPatch = backgroundPatch;
        this.click = click;
        setSize(width, height);
        setInteractGridSize(interactSize);
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        if (this.backgroundPatch != null) {
            this.backgroundPatch.draw(batch, getX(parent), getY(parent), getWidth(), getHeight());
        }
    }

    @Override
    public boolean click (GridPoint2 interactPos) {
        AudioPlayer.getInstance().playMusic(Sounds.ITEM_CLICK);

        if (this.click == null) return super.click(interactPos);

        return this.click.handle(this, interactPos);
    }

    /**
     * 点击按钮事件处理接口
     * */
    public interface Click {
        boolean handle (UIButton button, GridPoint2 interactPos);
    }
}
