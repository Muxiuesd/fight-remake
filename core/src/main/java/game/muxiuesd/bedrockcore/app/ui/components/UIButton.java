package game.muxiuesd.bedrockcore.app.ui.components;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    /// 按钮点击状态（方案 B：按下→松开才触发）
    private boolean pendingClick;                 //左键是否已按下、等待松开确认
    private GridPoint2 pendingInteractPos;        //按下时的交互坐标（松开触发时使用）

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
    public boolean click (GridPoint2 interactPos, int button) {
        //只有左键点击才走方案B（按下标记、松开确认触发），其他按键（右键等）由子类自行判断
        if (button != Input.Buttons.LEFT) return super.click(interactPos, button);

        //方案 B：左键按下仅记录待确认状态，不立即执行点击事件；
        //松开时（且鼠标仍在按钮上）才真正触发，见 {@link #update(float)}
        this.pendingClick = true;
        this.pendingInteractPos = interactPos;
        return false;
    }

    /**
     * 按钮点击状态的推进（方案 B）
     * <p>
     * 每帧检测：左键按下待确认期间——
     * ① 松开且鼠标仍在按钮上：触发点击事件 + 结束按下态；
     * ② 按住但鼠标移出按钮：取消点击（结束按下态，不触发）。
     * ③ 按钮不可见/不可交互：立即取消（防止隐藏/移除时残留误触发）。
     */
    @Override
    public void update (float delta) {
        //防御：不可见或不可交互时取消待确认的点击，避免隐藏/禁用状态下误触发
        if (!this.isVisible() || !this.isEnabled()) {
            this.endPendingClick();
            super.update(delta);
            return;
        }
        if (this.pendingClick) {
            boolean leftPressed = Gdx.input.isButtonPressed(Input.Buttons.LEFT);
            if (!leftPressed) {
                //左键已松开
                if (this.isMouseOver()) {
                    //松开时鼠标仍在按钮上：触发点击事件 + 播放点击音效
                    this.playClickSound();
                    if (this.clickEvent != null) {
                        this.clickEvent.handle(this, this.pendingInteractPos);
                    }
                }
                //无论是否在按钮上松开，都结束按下态
                this.endPendingClick();
            } else if (!this.isMouseOver()) {
                //按住但鼠标移出按钮：取消本次点击（不触发）
                this.endPendingClick();
            }
        }
        super.update(delta);
    }

    /**
     * 取消待确认的点击状态（外部移除/隐藏组件时可调用，防止状态残留）
     */
    public void cancelPendingClick () {
        this.endPendingClick();
    }

    /**
     * 结束待确认的点击状态：清除标记与按下态
     */
    private void endPendingClick () {
        this.pendingClick = false;
        this.pendingInteractPos = null;
        this.setClicked(false);
    }

    @Override
    public void mouseOver (GridPoint2 interactPos) {
        if (this.mouseOverEvent == null) {
            super.mouseOver(interactPos);
            return;
        }
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
