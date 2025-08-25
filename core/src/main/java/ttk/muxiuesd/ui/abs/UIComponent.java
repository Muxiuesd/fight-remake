package ttk.muxiuesd.ui.abs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;

/**
 * 基础UI组件
 * */
public abstract class UIComponent implements Updateable, Drawable, ShapeRenderable {

    protected float x, y;
    protected float width, height;
    ///交互区域网格
    private GridPoint2 interactGrid;

    protected boolean visible = true;
    protected boolean enabled = true;
    protected int zIndex = 0; // 渲染顺序

    public UIComponent(float x, float y, float width, float height, GridPoint2 interactGrid) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.interactGrid = interactGrid;
    }

    @Override
    public void update (float delta) {
    }

    @Override
    public void draw (Batch batch) {
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
    }

    /**
     * 鼠标指针放在这个UI组件上
     * @param interactPos 鼠标放在这个UI组件的交互区域的坐标位置
     * */
    public void mouseOver (GridPoint2 interactPos) {
    }

    /**
     * 鼠标点击这个UI组件
     * @param interactPos 鼠标点击这个UI组件的交互区域的坐标位置
     * @return 是否具有传递性，比如这个组件位于另一个组件之上，有传递性则执行完这个组件的方法后继续下一个组件的方法
     * */
    public boolean click (GridPoint2 interactPos) {
        //默认不具有传递性
        return false;
    }

    public Vector2 getPosition () {
        return new Vector2(x, y);
    }
    public float getX() { return x; }
    public float getY() { return y; }
    public Vector2 getSize() {
        return new Vector2(width, height);
    }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public boolean isVisible() { return visible; }
    public boolean isEnabled() { return enabled; }
    public int getZIndex() { return zIndex; }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
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

    public GridPoint2 getInteractGrid () {
        return this.interactGrid;
    }

    public UIComponent setInteractGrid (GridPoint2 interactGrid) {
        this.interactGrid = interactGrid;
        return this;
    }
}
