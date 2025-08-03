package ttk.muxiuesd.interfaces.world.item;

import com.badlogic.gdx.graphics.g2d.Batch;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品的渲染
 * */
public interface ItemRenderable {
    void drawOnHand (Batch batch, LivingEntity<?> holder, ItemStack itemStack);
    void drawOnWorld (Batch batch, ItemEntity itemEntity);
}
