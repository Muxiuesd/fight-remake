package ttk.muxiuesd.world.entity.genfactory;

import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.math.Vec2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.entity.EntityGenFactory;
import ttk.muxiuesd.pool.entity.ItemEntityPool;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 物品实体的生成工厂
 * */
public class ItemEntityGetter implements EntityGenFactory<ItemEntity> {
    public static ItemEntityPool POOL = new ItemEntityPool();


    /**
     * 直接获取一个可以立即被捡起来的物品实体
     * */
    public static ItemEntity getPickUpable (EntitySystem es, Vector2 pos, ItemStack itemStack) {
        return get(es, pos, itemStack)
            .setLivingTime(Fight.ITEM_ENTITY_PICKUP_SPAN.getValue());
    }
    public static ItemEntity getPickUpable (EntitySystem es, Vec2 pos, ItemStack itemStack) {
        return get(es, pos, itemStack)
            .setLivingTime(Fight.ITEM_ENTITY_PICKUP_SPAN.getValue());
    }

    /**
     * 拿取池中的物品实体
     * */
    public static ItemEntity get (EntitySystem es, Vec2 pos, ItemStack itemStack) {
        return get(es, itemStack).setPosition(pos.getX(), pos.getY());
    }

    /**
     * 拿取池中的物品实体
     * */
    public static ItemEntity get (EntitySystem es, Vector2 position, ItemStack itemStack) {
        return get(es, itemStack).setPosition(position);
    }

    /**
     * 拿取池中的物品实体，自动添加进实体系统 {@link EntitySystem}
     * */
    public static ItemEntity get (EntitySystem es, ItemStack itemStack) {
        ItemEntity entity = POOL.obtain();
        entity.setEntitySystem(es);
        entity.setItemStack(itemStack);
        es.add(entity);
        //默认大小
        return entity.setSize(ItemEntity.DEFAULT_SIZE);
    }
}
