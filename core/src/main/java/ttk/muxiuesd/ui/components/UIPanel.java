package ttk.muxiuesd.ui.components;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.GridPoint2;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.gui.UIComponentsHolder;
import ttk.muxiuesd.ui.abs.UIComponent;
import ttk.muxiuesd.util.Util;

/**
 * UI组件的面板
 * <p>
 * UI组件的一个容器，组件在面板里面时，组件的坐标都是相对于面板的坐标
 * */
public class UIPanel extends UIComponent implements UIComponentsHolder {
    public UIPanel (float x, float y, float width, float height, GridPoint2 interactGridSize) {
        super(x, y, width, height, interactGridSize);
    }

    @Override
    public void draw (Batch batch, UIComponent parent) {
        getComponents().forEach(component -> component.draw(batch, this));
    }

    @Override
    public boolean click (GridPoint2 interactPos) {
        //面板内部坐标
        Vector2 internalPos = new Vector2(interactPos.x, interactPos.y);
        Rectangle rectangle = new Rectangle();

        //遍历面板里面的组件，用内部坐标来检测
        for (UIComponent component : getComponents()) {
            rectangle.set(component.getX(), component.getY(), component.getWidth(), component.getHeight());
            if (rectangle.contains(internalPos)) {
                //计算交互区域坐标
                GridPoint2 interactGridPos = Util.getInteractGridPos(
                    component.getPosition(),
                    internalPos,
                    component.getSize(),
                    component.getInteractGridSize());
                if (! component.click(interactGridPos)) break;
            }
        }
        return super.click(interactPos);
    }
}
