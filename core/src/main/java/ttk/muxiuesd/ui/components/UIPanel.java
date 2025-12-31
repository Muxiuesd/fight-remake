package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.gui.UIComponentsHolder;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.util.pool.PoolableRectangle;

import java.util.LinkedHashSet;

/**
 * UI组件的面板
 * <p>
 * UI组件的一个容器，组件在面板里面时，组件的坐标都是相对于面板的坐标。
 * <p>
 * 面板可以互相嵌套
 * */
public class UIPanel extends UIComponent implements UIComponentsHolder {
    private LinkedHashSet<UIComponent> components;
    //父节点面板
    private UIPanel parent;

    public UIPanel() {}
    public UIPanel (float x, float y, float width, float height, GridPoint2 interactGridSize) {
        super(x, y, width, height, interactGridSize);
        this.components = new LinkedHashSet<>();
    }

    @Override
    public void update (float delta) {
        this.getComponents().forEach(component -> component.update(delta));
    }

    @Override
    public void draw (Batch batch, UIPanel parent) {
        this.getComponents().forEach(component -> component.draw(batch, this));
    }

    @Override
    public boolean click (GridPoint2 interactPos) {
        if (! this.getComponents().isEmpty()) {
            //面板内部坐标
            Vector2 internalPos = new Vector2(interactPos.x, interactPos.y);
            PoolableRectangle rectangle = Pools.RECT.obtain();

            //遍历面板里面的组件，用内部坐标来检测
            for (UIComponent component : this.getComponents()) {
                component.setClicked(false);
                //如果是不可交互状态的组件就直接跳过
                if (!component.isEnabled()) continue;

                rectangle.set(component.getX(), component.getY(), component.getWidth(), component.getHeight());
                if (rectangle.contains(internalPos)) {

                    //计算交互区域坐标
                    GridPoint2 interactGridPos = Util.getInteractGridPos(
                        component.getPosition(),
                        internalPos,
                        component.getSize(),
                        component.getInteractGridSize()
                    );
                    //设置被点击
                    component.setClicked(true);
                    if (! component.click(interactGridPos)) break;
                }
            }

            Pools.RECT.free(rectangle);
        }
        return super.click(interactPos);
    }

    @Override
    public void mouseOver (GridPoint2 interactPos) {
        if (! this.getComponents().isEmpty()) {
            //面板内部坐标
            Vector2 internalPos = new Vector2(interactPos.x, interactPos.y);
            PoolableRectangle rectangle = Pools.RECT.obtain();

            //遍历面板里面的组件，用内部坐标来检测
            for (UIComponent component : this.getComponents()) {
                component.setMouseOver(false);
                //如果是不可交互状态的组件就直接跳过
                if (!component.isEnabled()) continue;

                rectangle.set(component.getX(), component.getY(), component.getWidth(), component.getHeight());
                if (rectangle.contains(internalPos)) {
                    //计算交互区域坐标
                    GridPoint2 interactGridPos = Util.getInteractGridPos(
                        component.getPosition(),
                        internalPos,
                        component.getSize(),
                        component.getInteractGridSize()
                    );
                    component.setMouseOver(true);
                    component.mouseOver(interactGridPos);
                }
            }

            Pools.RECT.free(rectangle);
        }
        super.mouseOver(interactPos);
    }

    @Override
    public UIPanel setMouseOver (boolean mouseOver) {
        super.setMouseOver(mouseOver);
        //当这个面板都没有被鼠标覆盖，就让里面的所有ui组件的覆盖状态都为false
        if (!mouseOver) {
            this.getComponents().forEach(component -> component.setMouseOver(false));
        }
        return this;
    }

    @Override
    public void addComponent (UIComponent component) {
        UIComponentsHolder.super.addComponent(component);
        //当加入的组件是面板时，把子面板的父节点设置为此面板
        if (component instanceof UIPanel childPanel) {
            childPanel.setParent(this);
        }
    }

    @Override
    public void removeComponent (UIComponent component) {
        UIComponentsHolder.super.removeComponent(component);
        //当被移除的组件是面板时，把子面板的父节点设置为空
        if (component instanceof UIPanel childPanel) {
            childPanel.setParent(null);
        }
    }

    @Override
    public LinkedHashSet<UIComponent> getComponents () {
        return this.components;
    }

    public UIPanel getParent () {
        return this.parent;
    }

    public UIPanel setParent (UIPanel parent) {
        this.parent = parent;
        return this;
    }

    @Override
    public float getX () {
        float x = super.getX();
        return this.getParent() == null ? x : x + this.getParent().getX();
    }

    @Override
    public float getY () {
        float y = super.getY();
        return this.getParent() == null ? y : y + this.getParent().getY();
    }

    @Override
    public UIComponent setClicked (boolean clicked) {
        //如果面板被设置为没有被点击，那么里面的所有组件都应该是没有被点击的状态
        if (!clicked && !this.getComponents().isEmpty()) {
            this.getComponents().forEach(component -> component.setClicked(false));
        }
        return super.setClicked(clicked);
    }
}
