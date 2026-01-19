package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.gui.UIComponentsHolder;
import ttk.muxiuesd.interfaces.util.Voidable;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.ui.abs.UIScreen;
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
public class UIPanel extends UIComponent implements UIComponentsHolder, Voidable {
    public static final UIPanel VOID_INSTANCE = new UIPanel();
    static {
        VOID_INSTANCE.setPosition(0, 0).setSize(0, 0);
    }

    private LinkedHashSet<UIComponent> components;

    public UIPanel() {}
    public UIPanel (float x, float y) {
        this(x, y, 0, 0, new GridPoint2());
    }
    public UIPanel (float x, float y, GridPoint2 interactGridSize) {
        this(x, y, 0, 0, interactGridSize);
    }
    public UIPanel (float x, float y, float width, float height, GridPoint2 interactGridSize) {
        super(x, y, width, height, interactGridSize);
        this.components = new LinkedHashSet<>();
    }

    /**
     * 面板的自动设置方法
     * */
    public UIPanel auto () {
        this.autoSize();
        this.autoInteractGridSize();
        return this;
    }

    /**
     * 根据持有的UI组件大小来自动计算大小面板的尺寸大小，在添加完UI组件后调用
     * */
    public UIPanel autoSize () {
        LinkedHashSet<UIComponent> uiComponents = this.getComponents();
        if (uiComponents.isEmpty()) return this;

        float maxWidth = getWidth();
        float maxHeight = getHeight();
        float left = 0f, right = 0f;
        float up = 0f, down = 0f;

        for (UIComponent component : uiComponents) {
            //找到组件的最左边
            if (component.getX() < left) {
                left = component.getX();
            }
            //找最右边
            float r = component.getX() + component.getWidth();
            if (r > right) {
                right = r;
            }

            /*//找组件的最大高度
            if (componentSize.y > maxHeight) {
                maxHeight = componentSize.y;
            }*/

            //找最低点
            if (component.getY() < down) {
                down = component.getY();
            }
            //找最高点
            float u = component.getY() + component.getHeight();
            if (u > up) {
                up = u;
            }
        }

        //取所有组件最高点和最低点的高度差和所有组件当中最大高度的最大值
        maxHeight = Math.max(Math.abs(up - down), maxHeight);
        maxWidth = Math.max(Math.abs(right - left), maxWidth);
        setSize(maxWidth, maxHeight);

        return this;
    }

    /**
     * 根据宽高自动设置交互网格尺寸
     * */
    public UIPanel autoInteractGridSize () {
        this.setInteractGridSize(new GridPoint2((int) getWidth(), (int) getHeight()));
        return this;
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
    public void renderShape (ShapeRenderer batch) {
        super.renderShape(batch);
        this.getComponents().forEach(component -> component.renderShape(batch));
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
                //记录这个ui上一个状态是否被鼠标覆盖
                boolean uiComponentMouseOver = component.isMouseOver();
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

                //鼠标上一个状态是被鼠标覆盖的，但是此时的状态不是，就调用方法
                if (uiComponentMouseOver && !component.isMouseOver()) {
                    component.mouseDown();
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
        component.setScreen(getScreen());
        component.setParentPanel(this);
    }

    @Override
    public void removeComponent (UIComponent component) {
        UIComponentsHolder.super.removeComponent(component);
        component.setScreen(null);
        component.setParentPanel(VOID_INSTANCE);

    }

    @Override
    public LinkedHashSet<UIComponent> getComponents () {
        return this.components;
    }

    @Override
    public UIPanel setScreen (UIScreen screen) {
        super.setScreen(screen);
        //将所有子节点的UI组件的screen都设置成指定的screen
        getComponents().forEach(component -> component.setScreen(screen));
        return this;
    }

    @Override
    public UIPanel setClicked (boolean clicked) {
        //如果面板被设置为没有被点击，那么里面的所有组件都应该是没有被点击的状态
        if (!clicked && !this.getComponents().isEmpty()) {
            this.getComponents().forEach(component -> component.setClicked(false));
        }
        super.setClicked(clicked);
        return this;
    }
}
