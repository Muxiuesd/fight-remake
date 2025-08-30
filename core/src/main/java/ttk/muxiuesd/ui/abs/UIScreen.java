package ttk.muxiuesd.ui.abs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.interfaces.gui.GUIResize;
import ttk.muxiuesd.util.Util;

import java.util.LinkedHashSet;

/**
 * UI屏幕，UI组件都绘制在这个Screen里面
 * */
public abstract class UIScreen implements Updateable, Drawable, ShapeRenderable, GUIResize {
    private boolean mouseOver = false;  ///当鼠标指针在任意的组件上就标记为true，否则为false

    private final LinkedHashSet<UIComponent> uiComponents;

    public UIScreen () {
        this.uiComponents = new LinkedHashSet<>();
    }

    @Override
    public void update (float delta) {
        this.setMouseOver(false);
        if (this.getComponents().isEmpty()) return;

        Vector2 mouseUIPosition = Util.getMouseUIPosition();
        //重复利用的矩形区域
        Rectangle rectangle = new Rectangle();
        this.getComponents().forEach(uiComponent -> {
            uiComponent.update(delta);

            rectangle.set(uiComponent.getX(), uiComponent.getY(), uiComponent.getWidth(), uiComponent.getHeight());
            //鼠标坐标在ui的区域上
            if (rectangle.contains(mouseUIPosition)) {
                //计算交互区域坐标
                GridPoint2 interactGrid = uiComponent.getInteractGridSize();
                Vector2 position = uiComponent.getPosition();
                Vector2 size = uiComponent.getSize();
                int xn = (int) ((mouseUIPosition.x - position.x) / size.x * interactGrid.x);
                int yn = (int) ((mouseUIPosition.y - position.y) / size.y * interactGrid.y);
                GridPoint2 grid = new GridPoint2(xn, yn);
                uiComponent.mouseOver(grid);

                //如果鼠标在组件的交互区域上并且点击了鼠标左键，就是点击了组件
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    uiComponent.click(grid);
                }

                this.setMouseOver(true);
            }
        });
    }

    @Override
    public void draw (Batch batch) {
        if (this.getComponents().isEmpty()) return;
        this.getComponents().forEach(uiComponent -> uiComponent.draw(batch));
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        if (this.getComponents().isEmpty()) return;
        this.getComponents().forEach(uiComponent -> uiComponent.renderShape(batch));
    }

    /**
     * 当相机视口大小更改时调用
     * */
    @Override
    public void resize (float width, float height) {
        this.getComponents().forEach(uiComponent -> uiComponent.resize(width, height));
    }

    public void addComponent (UIComponent component) {
        this.getComponents().add(component);
    }

    public void removeComponent (UIComponent component) {
        this.getComponents().remove(component);
    }

    public LinkedHashSet<UIComponent> getComponents () {
        return this.uiComponents;
    }

    public boolean isMouseOver () {
        return this.mouseOver;
    }

    public void setMouseOver (boolean mouseOver) {
        this.mouseOver = mouseOver;
    }
}
