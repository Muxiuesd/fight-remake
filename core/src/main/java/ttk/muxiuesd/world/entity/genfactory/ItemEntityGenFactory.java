package ttk.muxiuesd.world.entity.genfactory;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.world.entity.EntityGenFactory;
import ttk.muxiuesd.pool.entity.ItemEntityPool;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品实体生成工厂
 * */
public class ItemEntityGenFactory implements EntityGenFactory<ItemEntity> {
    public static ItemEntityPool POOL = new ItemEntityPool();

    public static ItemEntity create (EntitySystem entitySystem, Vector2 position, ItemStack itemStack) {
        ItemEntity entity = POOL.obtain();
        //ItemEntity entity = (ItemEntity) Gets.ENTITY(Entities.ITEM_ENTITY, entitySystem);
        entity.setEntitySystem(entitySystem);
        entity.setPosition(position);
        entity.setItemStack(itemStack);
        return entity;
    }
}
