package ttk.muxiuesd.pool.entity;

import com.badlogic.gdx.utils.Pool;
import ttk.muxiuesd.registry.Entities;
import ttk.muxiuesd.registry.EntityTypes;
import ttk.muxiuesd.world.entity.ItemEntity;

/**
 * 物品实体对象池
 * */
public class ItemEntityPool extends Pool<ItemEntity> {
    public ItemEntityPool () {
        super(30);
    }

    @Override
    protected ItemEntity newObject () {
        return Entities.ITEM_ENTITY.create(null, EntityTypes.ITEM_ENTITY);
    }
}
