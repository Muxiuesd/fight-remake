package game.muxiuesd.bedrockcore.app.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.GridPoint2;
import game.muxiuesd.bedrockcore.app.interfaces.Voidable;
import game.muxiuesd.bedrockcore.app.ui.abs.UIComponent;
import game.muxiuesd.bedrockcore.util.TextureUtil;

/**
 * 单纯的按钮组件，没有字体文本渲染
 * */
public class UIButton extends UIComponent {
    public static final MouseOverEvent VOID_MOUSE_OVER_EVENT = (button, interactPos) -> {};
    public static final ClickEvent VOID_CLICK_EVENT = (button, interactPos) -> {
        //啥也不做
        return false;
    };
    public static final int DEFAULT_EDGE = 3;
    public static final float DEFAULT_WIDTH = 80f;
    public static final float DEFAULT_HEIGHT = 14f;


    private NinePatch backgroundPatch;
    private NinePatch clickBackgroundPatch;
    private NinePatch mouseOverBackgroundPatch;
    private ClickEvent clickEvent;
    private MouseOverEvent mouseOverEvent;

    /**
     * @param background 背景的材质贴图
     * @param clickBackground 点击按钮后的材质贴图
     * @param mouseOverBackground 鼠标放在按钮上的材质贴图
     * */
    public UIButton(TextureRegion background, TextureRegion clickBackground, TextureRegion mouseOverBackground,
                    ClickEvent clickEvent, MouseOverEvent mouseOverEvent) {
        this(
            TextureUtil.createNinePatch(background, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE),
            TextureUtil.createNinePatch(clickBackground, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE),
            TextureUtil.createNinePatch(mouseOverBackground, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE, DEFAULT_EDGE),
            DEFAULT_WIDTH, DEFAULT_HEIGHT, new GridPoint2((int) DEFAULT_WIDTH, (int) DEFAULT_HEIGHT),
            clickEvent, mouseOverEvent
        );
    }

    public UIButton (NinePatch backgroundPatch, NinePatch clickBackground, NinePatch mouseOverBackground,
                     float width, float height, GridPoint2 interactSize,
                     ClickEvent clickEvent, MouseOverEvent mouseOverEvent) {
        this.backgroundPatch = backgroundPatch;
        this.clickBackgroundPatch = clickBackground;
        this.mouseOverBackgroundPatch = mouseOverBackground;
        this.clickEvent = clickEvent;
        this.mouseOverEvent = mouseOverEvent;

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
        //AudioPlayer.getInstance().playMusic(Sounds.ITEM_CLICK);
        this.playClickSound();

        if (this.clickEvent == null) return super.click(interactPos);

        return this.clickEvent.handle(this, interactPos);
    }

    @Override
    public void mouseOver (GridPoint2 interactPos) {
        if (this.mouseOverEvent == null) super.mouseOver(interactPos);
        this.mouseOverEvent.handle(this, interactPos);
    }

    /**
     * 播放点击音效，需要自己实现
     * */
    public void playClickSound () {}

    public NinePatch getBackgroundPatch () {
        return this.backgroundPatch;
    }

    public UIButton setBackgroundPatch (NinePatch backgroundPatch) {
        this.backgroundPatch = backgroundPatch;
        return this;
    }

    public NinePatch getClickBackgroundPatch () {
        return this.clickBackgroundPatch;
    }

    public UIButton setClickBackgroundPatch (NinePatch clickBackgroundPatch) {
        this.clickBackgroundPatch = clickBackgroundPatch;
        return this;
    }

    public NinePatch getMouseOverBackgroundPatch () {
        return this.mouseOverBackgroundPatch;
    }

    public UIButton setMouseOverBackgroundPatch (NinePatch mouseOverBackgroundPatch) {
        this.mouseOverBackgroundPatch = mouseOverBackgroundPatch;
        return this;
    }

    public ClickEvent getClickEvent () {
        return this.clickEvent;
    }

    public UIButton setClickEvent (ClickEvent clickEvent) {
        this.clickEvent = clickEvent;
        return this;
    }

    public MouseOverEvent getMouseOverEvent () {
        return this.mouseOverEvent;
    }

    public UIButton setMouseOverEvent (MouseOverEvent mouseOverEvent) {
        this.mouseOverEvent = mouseOverEvent;
        return this;
    }

    /**
     * 点击按钮事件处理接口
     * */
    public interface ClickEvent extends Voidable {
        boolean handle (UIButton button, GridPoint2 interactPos);
    }

    /**
     * 鼠标放在按钮上面的事件处理接口
     * */
    public interface MouseOverEvent extends Voidable {
        void handle (UIButton button, GridPoint2 interactPos);
    }
}
