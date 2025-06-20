package ttk.muxiuesd.interfaces.world.item;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品的shapeRender
 * */
public interface ItemShapeRenderable {
    void renderShape (ShapeRenderer batch, ItemStack itemStack);
}
