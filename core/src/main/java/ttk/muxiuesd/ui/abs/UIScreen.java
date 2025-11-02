package ttk.muxiuesd.ui.abs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;
import ttk.muxiuesd.interfaces.gui.GUIResize;
import ttk.muxiuesd.interfaces.gui.UIComponentsHolder;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.util.pool.PoolableRectangle;

import java.util.LinkedHashSet;

/**
 * UI屏幕，UI组件都绘制在这个Screen里面
 * */
public abstract class UIScreen implements Updateable, Drawable, ShapeRenderable, GUIResize, UIComponentsHolder {
    private final LinkedHashSet<UIComponent> components = new LinkedHashSet<>();;

    private boolean mouseOver = false;  ///当鼠标指针在任意的可交互的组件上就标记为true，否则为false

    public UIScreen () {
    }

    /**
     * 被展示出来时调用
     * */
    public void show () {}

    /**
     * 被隐藏时调用
     * */
    public void hide () {}

    @Override
    public void update (float delta) {
        setMouseOver(false);    //清理标记
        //没东西就直接返回
        if (getComponents().isEmpty()) return;

        Vector2 mouseUIPosition = Util.getMouseUIPosition();
        //重复利用的矩形区域
        PoolableRectangle rectangle = Pools.RECT.obtain();

        for (UIComponent uiComponent : getComponents()) {
            //更新组件
            uiComponent.update(delta);
            //不可交互状态的组件就直接跳过交互计算
            if (!uiComponent.isEnabled()) continue;

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
        }
        Pools.RECT.free(rectangle);
    }

    @Override
    public void draw (Batch batch) {
        if (getComponents().isEmpty()) return;
        getComponents().forEach(uiComponent -> uiComponent.draw(batch, null));
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        if (getComponents().isEmpty()) return;
        getComponents().forEach(uiComponent -> uiComponent.renderShape(batch));
    }

    /**
     * 当相机视口大小更改时调用
     * */
    @Override
    public void resize (float width, float height) {
        getComponents().forEach(uiComponent -> uiComponent.resize(width, height));
    }

    public boolean isMouseOver () {
        return this.mouseOver;
    }

    public void setMouseOver (boolean mouseOver) {
        this.mouseOver = mouseOver;
    }

    @Override
    public LinkedHashSet<UIComponent> getComponents () {
        return this.components;
    }
}
