package game.muxiuesd.bedrockcore.app.ui.abs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.app.interfaces.Updateable;
import game.muxiuesd.bedrockcore.app.interfaces.render.Drawable;
import game.muxiuesd.bedrockcore.app.interfaces.render.ShapeRenderable;
import game.muxiuesd.bedrockcore.app.interfaces.ui.GUIResize;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.gui.UIComponentsHolder;
import ttk.muxiuesd.util.Util;

import java.util.LinkedHashSet;

/**
 * UI屏幕，UI组件都绘制在这个Screen里面
 * <p>
 * 需要自己实现 {@link UIComponentsHolder#getComponents()}
 * <p>
 * 如果要处理全局的键盘和鼠标的输入，就把实例加入gdx的input处理
 * */
public abstract class UIScreen
    implements Updateable, Drawable, ShapeRenderable, GUIResize, UIComponentsHolder, InputProcessor {

    private final LinkedHashSet<UIComponent> components = new LinkedHashSet<>();
    private final LinkedHashSet<UIComponent> delayAddComponents = new LinkedHashSet<>();
    private final LinkedHashSet<UIComponent> delayRemoveComponents = new LinkedHashSet<>();
    private final Rectangle rectangle = new Rectangle();  ///重复利用的矩形区域
    private final Vector2 mouseUIPosition = new Vector2();   ///重复利用的鼠标坐标
    private final GridPoint2 interactGrid = new GridPoint2();   ///重复利用的交互网格坐标
    private UIComponent dragComponent;   ///当前正在拖拽的组件（拖拽捕获）

    private boolean mouseOver = false;  ///当鼠标指针在任意的可交互的组件上就标记为true，否则为false
    private UIComponent focusComponent; ///焦点组件，当有焦点组件时，键盘输入只在焦点组件里生效


    public UIScreen () {}

    /**
     * 被展示出来时调用
     * */
    public void show () {
    }

    /**
     * 被隐藏时调用
     * */
    public void hide () {
        this.getComponents().forEach(uiComponent -> {
            uiComponent.setMouseOver(false);
            uiComponent.setClicked(false);
        });
        //隐藏屏幕时清空焦点，防止焦点组件残留
        this.setFocusComponent(null);
        this.dragComponent = null;
    }

    @Override
    public void addComponent (UIComponent component) {
        this.delayAddComponents.add(component);
    }

    @Override
    public void removeComponent (UIComponent component) {
        this.delayRemoveComponents.add(component);
    }

    /**
     * UIScreen的核心更新逻辑
     * */
    @Override
    public void update (float delta) {
        //检查延迟添加和延迟删除
        this.handleDelayEvents();

        //清理标记
        setMouseOver(false);
        //没东西就直接返回
        if (getComponents().isEmpty()) return;
        boolean clickFlag = false;  //没有点到任何ui组件时就是false
        boolean dragHandled = false;  //本帧拖拽组件是否已经收到过拖拽事件
        this.mouseUIPosition.set(Util.getMouseUIPosition());


        for (UIComponent uiComponent : getComponents()) {
            //更新组件
            uiComponent.update(delta);
            //记录这个ui上一个状态是否被鼠标覆盖
            boolean uiComponentMouseOver = uiComponent.isMouseOver();
            //不可交互状态或不可见的组件就直接跳过交互计算
            if (!uiComponent.isEnabled() || !uiComponent.isVisible()) {
                //跳过前先清空状态，防止残留hover/clicked
                if (uiComponentMouseOver) {
                    uiComponent.setMouseOver(false);
                    uiComponent.mouseDown();
                }
                uiComponent.setClicked(false);
                continue;
            }

            this.rectangle.set(uiComponent.getX(), uiComponent.getY(), uiComponent.getWidth(), uiComponent.getHeight());
            //鼠标坐标在ui的区域上
            if (this.rectangle.contains(this.mouseUIPosition)) {
                //计算交互区域坐标
                GridPoint2 interactGridSize = uiComponent.getInteractGridSize();
                if (interactGridSize == null) {
                    //没有定义交互网格的组件不参与交互
                    if (uiComponentMouseOver) {
                        uiComponent.setMouseOver(false);
                        uiComponent.mouseDown();
                    }
                    uiComponent.setClicked(false);
                    continue;
                }
                //用标量getter避免每帧new Vector2
                float compX = uiComponent.getX();
                float compY = uiComponent.getY();
                float compWidth = uiComponent.getWidth();
                float compHeight = uiComponent.getHeight();
                //计算鼠标相对于UI组件的坐标
                int xn = (int) ((this.mouseUIPosition.x - compX) / compWidth * interactGridSize.x);
                int yn = (int) ((this.mouseUIPosition.y - compY) / compHeight * interactGridSize.y);
                this.interactGrid.set(xn, yn);
                uiComponent.setMouseOver(true);
                uiComponent.mouseOver(this.interactGrid);

                //如果鼠标在组件的交互区域上并且点击了鼠标左键，就是点击了组件
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    uiComponent
                        .setClicked(true)
                        .click(this.interactGrid, Input.Buttons.LEFT);
                    clickFlag = true;
                    dragHandled = true;
                    //记录拖拽起点：按住左键后即使移出组件矩形也继续拖拽
                    this.dragComponent = uiComponent;
                    //检查鼠标点击的组件是否是目前的焦点组件，不是就清空焦点
                    if (this.getFocusComponent() != null && this.getFocusComponent() != uiComponent) {
                        this.setFocusComponent(null);
                    }
                }else if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                    //如果是按住鼠标左键，就是拖拽
                    uiComponent.mouseDrag(this.mouseUIPosition.x - compX, this.mouseUIPosition.y - compY);
                    clickFlag = true;
                    dragHandled = true;
                    this.dragComponent = uiComponent;
                    //检查鼠标点击的组件是否是目前的焦点组件，不是就清空焦点
                    if (this.getFocusComponent() != null && this.getFocusComponent() != uiComponent) {
                        this.setFocusComponent(null);
                    }
                }else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                    //右键点击组件，由组件自己在click方法里判断左右键
                    uiComponent.click(this.interactGrid, Input.Buttons.RIGHT);
                    clickFlag = true;
                }
                //这个ui屏幕的状态变成被鼠标覆盖
                this.setMouseOver(true);
            }else {
                //鼠标不在ui的区域上
                if (this.dragComponent != uiComponent) {
                    //没有拖拽这个组件的就清除状态
                    uiComponent
                        .setClicked(false)
                        .setMouseOver(false);
                }
            }
            //鼠标上一个状态是被鼠标覆盖的，但是此时的状态不是，就调用方法
            if (uiComponentMouseOver && !uiComponent.isMouseOver()) {
                uiComponent.mouseDown();
            }
        }

        //拖拽捕获：正在拖拽的组件即使鼠标移出矩形区域也继续拖拽
        if (this.dragComponent != null) {
            if (!Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                //松开左键，结束拖拽
                this.dragComponent = null;
            }else if (!dragHandled) {
                //鼠标已移出组件矩形（本帧未通过矩形检测触发拖拽），直接用相对组件的坐标继续拖拽
                this.dragComponent.mouseDrag(
                    this.mouseUIPosition.x - this.dragComponent.getX(),
                    this.mouseUIPosition.y - this.dragComponent.getY()
                );
                clickFlag = true;
                this.setMouseOver(true);
            }
        }

        if (!clickFlag) {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                //本帧点击了屏幕上没有任何UI组件的空白区域
                this.onClickBlank(Input.Buttons.LEFT);
            }else if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
                this.onClickBlank(Input.Buttons.RIGHT);
            }
        }

        if (!clickFlag
            && (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) || Gdx.input.isButtonPressed(Input.Buttons.LEFT))
        ) {
            //这时候点击鼠标，就会失去已有的焦点
            this.setFocusComponent(null);
        }

        //还原
        this.rectangle.set(0, 0, 0, 0);
    }

    /**
     * 点击了屏幕上没有任何UI组件的空白区域时调用
     * <p>
     * 只有按下鼠标左键或右键的那一帧才会调用，按住不放不会重复调用
     * @param button 按下的是哪个鼠标按键（Input.Buttons.LEFT / Input.Buttons.RIGHT）
     * */
    protected void onClickBlank (int button) {
    }

    /**
     * 处理延迟添加和删除
     * */
    public void handleDelayEvents () {
        if (!this.delayAddComponents.isEmpty()) {
            this.delayAddComponents.forEach(delayAddComponent -> {
                delayAddComponent.setScreen(this);
                this.components.add(delayAddComponent);
            });
            this.delayAddComponents.clear();
            //添加完新的组件后调用一次排序
            sortComponents();
        }
        if (!this.delayRemoveComponents.isEmpty()) {
            this.delayRemoveComponents.forEach(delayRemoveComponent -> {
                delayRemoveComponent.setScreen(null);
                this.components.remove(delayRemoveComponent);
                //如果移除的是焦点组件，就清空焦点
                if (this.getFocusComponent() == delayRemoveComponent) {
                    this.setFocusComponent(null);
                }
                //如果移除的是正在拖拽的组件，就结束拖拽
                if (this.dragComponent == delayRemoveComponent) {
                    this.dragComponent = null;
                }
            });
            this.delayRemoveComponents.clear();
        }
    }

    @Override
    public void draw (Batch batch) {
        if (getComponents().isEmpty()) return;
        getComponents().forEach(uiComponent -> {
            if (uiComponent.isVisible()) uiComponent.draw(batch, null);
        });
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        if (!Fight.UI_DEBUG_BOX_RENDER.getValue() || getComponents().isEmpty()) return;
        getComponents().forEach(uiComponent -> uiComponent.renderShape(batch));
    }

    /**
     * 当相机视口大小更改时调用
     * */
    @Override
    public void resize (float width, float height) {
        if (getComponents().isEmpty()) return;
        getComponents().forEach(uiComponent -> uiComponent.resize(width, height));
    }

    @Override
    public boolean keyDown (int keycode) {
        UIComponent focus = this.getFocusComponent();
        if (focus != null) {
            focus.keyDown(keycode);
        }else if (!getComponents().isEmpty()) {
            getComponents().forEach(uiComponent -> uiComponent.keyDown(keycode));
        }

        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        UIComponent focus = this.getFocusComponent();
        if (focus != null) {
            focus.keyUp(keycode);
        }else if (!getComponents().isEmpty()) {
            getComponents().forEach(uiComponent -> uiComponent.keyUp(keycode));
        }
        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        UIComponent focus = this.getFocusComponent();
        if (focus != null) {
            focus.keyTyped(character);
        }else if (!getComponents().isEmpty()) {
            getComponents().forEach(uiComponent -> uiComponent.keyTyped(character));
        }

        return false;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        UIComponent focus = this.getFocusComponent();
        if (focus != null) {
            focus.touchDown(screenX, screenY, pointer, button);
        }else if (!getComponents().isEmpty()) {
            getComponents().forEach(uiComponent -> uiComponent.touchDown(screenX, screenY, pointer, button));
        }
        return false;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        UIComponent focus = this.getFocusComponent();
        if (focus != null) {
            focus.touchUp(screenX, screenY, pointer, button);
        }else if (!getComponents().isEmpty()) {
            getComponents().forEach(uiComponent -> uiComponent.touchUp(screenX, screenY, pointer, button));
        }
        return false;
    }

    @Override
    public boolean touchCancelled (int screenX, int screenY, int pointer, int button) {
        UIComponent focus = this.getFocusComponent();
        if (focus != null) {
            focus.touchCancelled(screenX, screenY, pointer, button);
        }else if (!getComponents().isEmpty()) {
            getComponents().forEach(uiComponent -> uiComponent.touchCancelled(screenX, screenY, pointer, button));
        }
        return false;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        UIComponent focus = this.getFocusComponent();
        if (focus != null) {
            focus.touchDragged(screenX, screenY, pointer);
        }else if (!getComponents().isEmpty()) {
            getComponents().forEach(uiComponent -> uiComponent.touchDragged(screenX, screenY, pointer));
        }
        return false;
    }

    @Override
    public boolean mouseMoved (int screenX, int screenY) {
        UIComponent focus = this.getFocusComponent();
        if (focus != null) {
            focus.mouseMoved(screenX, screenY);
        }else if (!getComponents().isEmpty()) {
            getComponents().forEach(uiComponent -> uiComponent.mouseMoved(screenX, screenY));
        }
        return false;
    }

    @Override
    public boolean scrolled (float amountX, float amountY) {
        UIComponent focus = this.getFocusComponent();
        if (focus != null) {
            focus.scrolled(amountX, amountY);
        }else if (!getComponents().isEmpty()) {
            getComponents().forEach(uiComponent -> uiComponent.scrolled(amountX, amountY));
        }
        return false;
    }

    public boolean isMouseOver () {
        return this.mouseOver;
    }

    public void setMouseOver (boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    /**
     * 获取焦点组件
     * */
    public UIComponent getFocusComponent () {
        return this.focusComponent;
    }

    /**
     * 设置焦点组件
     * */
    public UIScreen setFocusComponent (UIComponent focusComponent) {
        this.focusComponent = focusComponent;
        return this;
    }

    @Override
    public LinkedHashSet<UIComponent> getComponents () {
        return this.components;
    }
}
