package ttk.muxiuesd.ui.abs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.interfaces.gui.GUIDrawable;
import ttk.muxiuesd.interfaces.gui.GUIResize;
import ttk.muxiuesd.ui.components.UIPanel;

/**
 * 基础 UI 组件
 * */
public abstract class UIComponent implements Updateable, GUIDrawable, ShapeRenderable, GUIResize {

    private float x, y;
    private float width, height;
    private boolean visible = true;
    private boolean enabled = true;
    private int zIndex = 0; // 渲染顺序
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
        //batch.rect(this.getX(), this.getY(), this.getWidth(), this.getWidth());
    }

    /**
     * 鼠标指针放在这个UI组件上调用
     * @param interactPos 鼠标放在这个UI组件的交互区域的坐标位置
     * */
    public void mouseOver (GridPoint2 interactPos) {
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
     * 当相机视口大小更改时调用，传入的参数是改变大小后相机视口所能看到的宽高大小，单位：米
     * @param viewportWidth  视口宽度
     * @param viewportHeight 视口高度
     * */
    @Override
    public void resize (float viewportWidth, float viewportHeight) {
    }


    public Vector2 getPosition () {
        return new Vector2(this.getX(), this.getY());
    }

    /**
     * 获取原始的x
     * */
    public float getX () { return this.x; }

    /**
     * 获取原始的y
     * */
    public float getY () { return this.y; }

    /**
     * 根据父组件来获取x
     * */
    public float getX (UIComponent component) {
        return component != null ? this.getX() + component.getX() : this.getX();
    }

    /**
     * 根据父组件来获取y
     * */
    public float getY (UIComponent component) {
        return component != null ? this.getY() + component.getY() : this.getY();
    }

    public Vector2 getSize() {
        return new Vector2(this.width, this.height);
    }
    public float getWidth() { return this.width; }
    public float getHeight() { return this.height; }

    public boolean isVisible() { return this.visible; }

    public boolean isEnabled() { return this.enabled; }

    public int getZIndex() { return this.zIndex; }

    public void setPosition (Vector2 pos) {
        this.x = pos.x;
        this.y = pos.y;
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
}
