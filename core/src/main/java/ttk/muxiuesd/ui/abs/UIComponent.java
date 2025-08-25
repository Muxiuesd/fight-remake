package ttk.muxiuesd.ui.abs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;

/**
 * 基础UI组件
 * */
public abstract class UIComponent implements Updateable, Drawable, ShapeRenderable {
    protected float x, y;
    protected float width, height;
    protected boolean visible = true;
    protected boolean enabled = true;
    protected int zIndex = 0; // 渲染顺序

    public UIComponent(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
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


    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public boolean isVisible() { return visible; }
    public boolean isEnabled() { return enabled; }
    public int getZIndex() { return zIndex; }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setZIndex(int zIndex) {
        this.zIndex = zIndex;
    }
}
