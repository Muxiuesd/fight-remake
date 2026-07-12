package game.muxiuesd.bedrockcore.app.ui.abs;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.app.interfaces.Updateable;
import game.muxiuesd.bedrockcore.app.interfaces.render.ShapeRenderable;
import game.muxiuesd.bedrockcore.app.interfaces.ui.GUIDrawable;
import game.muxiuesd.bedrockcore.app.interfaces.ui.GUIResize;
import game.muxiuesd.bedrockcore.app.ui.components.UIPanel;

/**
 * 基础 UI 组件
 * <p>
 * 有实现 {@link InputProcessor} 这是对于全局的输入处理
 * */
public abstract class UIComponent implements Updateable, GUIDrawable, ShapeRenderable, GUIResize, InputProcessor {
    private UIScreen screen;  //隶属于哪一个screen
    private UIPanel parentPanel = UIPanel.VOID_INSTANCE;  //隶属于哪一个UI面板，无论如何不能为null
    private float x, y; //这里的坐标是相对于父组件的（如果有的话）
    private float width, height;
    private boolean visible = true;
    private boolean enabled = true;
    private int zIndex = 0;     // 渲染顺序
    ///交互区域网格
    private GridPoint2 interactGridSize;
    private boolean mouseIsOver = false;
    private boolean isClicked = false;

    public UIComponent() {}

    public UIComponent(float width, float height, GridPoint2 interactGridSize) {
        this(0, 0, width, height, interactGridSize);
    }
    public UIComponent(float x, float y, float width, float height, GridPoint2 interactGridSize) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.interactGridSize = interactGridSize;
    }



    @Override
    public void update (float delta) {
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        batch.rect(this.getAbsX(), this.getAbsY(), this.getWidth(), this.getHeight());
    }

    /**
     * 鼠标指针放在这个UI组件上调用
     * @param interactPos 鼠标放在这个UI组件的交互区域的坐标位置
     * */
    public void mouseOver (GridPoint2 interactPos) {
    }

    /**
     * 鼠标离开这个UI组件上面
     * */
    public void mouseDown () {
    }

    /**
     * 鼠标点击这个UI组件调用
     * @param interactPos 鼠标点击这个UI组件的交互区域的坐标位置
     * @return 是否具有传递性，比如这个组件位于另一个组件之上，有传递性则执行完这个组件的方法后继续下一个组件的方法
     * */
    public boolean click (GridPoint2 interactPos) {
        //默认不具有传递性
        return false;
    }

    /**
     * 鼠标对这个UI组件进行拖拽
     * @param mouseX 鼠标的横坐标（相对于UI组件自身）
     * @param mouseY 鼠标的纵坐标（相对于UI组件自身）
     * */
    public void mouseDrag (float mouseX, float mouseY) {
    }

    /**
     * 当相机视口大小更改时调用，传入的参数是改变大小后相机视口所能看到的宽高大小，单位：米
     * @param viewportWidth  视口宽度
     * @param viewportHeight 视口高度
     * */
    @Override
    public void resize (float viewportWidth, float viewportHeight) {
    }

    @Override
    public boolean keyDown (int keycode) {
        return false;
    }

    @Override
    public boolean keyUp (int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped (char character) {
        return false;
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled (int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved (int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled (float amountX, float amountY) {
        return false;
    }

    public UIScreen getScreen () {
        return this.screen;
    }

    public UIComponent setScreen (UIScreen screen) {
        this.screen = screen;
        return this;
    }

    public UIPanel getParentPanel () {
        return this.parentPanel;
    }

    public UIComponent setParentPanel (UIPanel parentPanel) {
        if (parentPanel == null){
            this.parentPanel = UIPanel.VOID_INSTANCE;
        }else {
            this.parentPanel = parentPanel;
        }
        return this;
    }

    public UIComponent resetParentPanel () {
        this.parentPanel = UIPanel.VOID_INSTANCE;
        return this;
    }

    public Vector2 getPosition () {
        return new Vector2(this.getX(), this.getY());
    }

    /**
     * 获取原始的x（相对于它的父组件）
     * */
    public float getX () {
        return this.x;
    }

    /**
     * 获取原始的y（相对于它的父组件）
     * */
    public float getY () {
        return this.y;
    }

    public UIComponent setX (float x) {
        this.x = x;
        return this;
    }

    public UIComponent setY (float y) {
        this.y = y;
        return this;
    }

    /**
     * 获取绝对的X坐标（相对于相机）
     * */
    public float getAbsX () {
        if (this.parentPanel == UIPanel.VOID_INSTANCE || this.parentPanel == null) {
            return this.x;
        }
        return this.x + this.parentPanel.getAbsX();
    }

    /**
     * 获取绝对的X坐标（相对于相机）
     * */
    public float getAbsY () {
        if (this.parentPanel == UIPanel.VOID_INSTANCE || this.parentPanel == null) {
            return this.y;
        }
        return this.y + this.parentPanel.getAbsY();
    }

    /**
     * 根据指定的父组件来获取x
     * */
    public float getX (UIComponent component) {
        return component != null ? this.getX() + component.getX() : this.getX();
    }

    /**
     * 根据指定的父组件来获取y
     * */
    public float getY (UIComponent component) {
        return component != null ? this.getY() + component.getY() : this.getY();
    }

    public Vector2 getSize() {
        return new Vector2(this.width, this.height);
    }
    public float getWidth() {
        return this.width;
    }

    public float getHeight() {
        return this.height;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public int getZIndex() {
        return this.zIndex;
    }

    public UIComponent setPosition (Vector2 pos) {
        this.x = pos.x;
        this.y = pos.y;
        return this;
    }

    public UIComponent setPosition (float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public UIComponent setSize(float width, float height) {
        this.width = width;
        this.height = height;
        return this;
    }

    public UIComponent setWidth (float width) {
        this.width = width;
        return this;
    }

    public UIComponent setHeight (float height) {
        this.height = height;
        return this;
    }

    public UIComponent setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    public UIComponent setEnabled(boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    public UIComponent setZIndex(int zIndex) {
        this.zIndex = zIndex;
        return this;
    }

    public GridPoint2 getInteractGridSize () {
        return this.interactGridSize;
    }

    public UIComponent setInteractGridSize (GridPoint2 interactGridSize) {
        this.interactGridSize = interactGridSize;
        return this;
    }

    public UIComponent setMouseOver (boolean mouseOver) {
        this.mouseIsOver = mouseOver;
        return this;
    }

    public boolean isMouseOver () {
        return this.mouseIsOver;
    }

    public boolean isClicked () {
        return this.isClicked;
    }

    public UIComponent setClicked (boolean clicked) {
        this.isClicked = clicked;
        return this;
    }

    /**
     * 检查组件是否是焦点
     * */
    public boolean isFocused () {
        if (this.getScreen() == null) return false;
        return this.getScreen().getFocusComponent() == this;
    }
}
