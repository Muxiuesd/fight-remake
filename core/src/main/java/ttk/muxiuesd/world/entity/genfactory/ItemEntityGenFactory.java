package ttk.muxiuesd.world.entity.genfactory;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.world.entity.EntityGenFactory;
import ttk.muxiuesd.pool.entity.ItemEntityPool;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品实体的生成工厂
 * */
public class ItemEntityGenFactory implements EntityGenFactory<ItemEntity> {
    public static ItemEntityPool POOL = new ItemEntityPool();

    /**
     * 拿取池中的实体，自动添加进实体系统 {@link EntitySystem}
     * */
    public static ItemEntity create (EntitySystem entitySystem, Vector2 position, ItemStack itemStack) {
        return create(entitySystem, itemStack).setPosition(position);
    }

    /**
     * 拿取池中的实体，自动添加进实体系统 {@link EntitySystem}
     * */
    public static ItemEntity create (EntitySystem entitySystem, ItemStack itemStack) {
        ItemEntity entity = POOL.obtain();
        entity.setEntitySystem(entitySystem);
        entity.setItemStack(itemStack);
        entitySystem.add(entity);
        //System.out.println("物品实体池拿取实体");
        return entity;
    }
}
