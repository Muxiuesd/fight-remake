package ttk.muxiuesd.pool.entity;

import com.badlogic.gdx.utils.Pool;
import ttk.muxiuesd.FightCore;
import ttk.muxiuesd.registry.Entities;
import ttk.muxiuesd.registry.EntityTypes;
import ttk.muxiuesd.world.entity.ItemEntity;

/**
 * 物品实体对象池
 * */
public class ItemEntityPool extends Pool<ItemEntity> {
    public ItemEntityPool () {
        super(0);
    }

    @Override
    protected ItemEntity newObject () {
        return Entities.ITEM_ENTITY.create(FightCore.getInstance().getWorld(), EntityTypes.ITEM_ENTITY);
    }
}
