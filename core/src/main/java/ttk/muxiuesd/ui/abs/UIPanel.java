package ttk.muxiuesd.ui.abs;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.interfaces.Drawable;
import ttk.muxiuesd.interfaces.ShapeRenderable;
import ttk.muxiuesd.interfaces.Updateable;

import java.util.LinkedHashSet;

/**
 * UI面板，UI组件都绘制在这里面
 * */
public abstract class UIPanel implements Updateable, Drawable, ShapeRenderable {
    private final LinkedHashSet<UIComponent> uiComponents;

    public UIPanel () {
        this.uiComponents = new LinkedHashSet<>();
    }

    @Override
    public void update (float delta) {
        this.getComponents().forEach(uiComponent -> uiComponent.update(delta));
    }

    @Override
    public void draw (Batch batch) {
        this.getComponents().forEach(uiComponent -> uiComponent.draw(batch));
    }

    @Override
    public void renderShape (ShapeRenderer batch) {
        this.getComponents().forEach(uiComponent -> uiComponent.renderShape(batch));
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
}
