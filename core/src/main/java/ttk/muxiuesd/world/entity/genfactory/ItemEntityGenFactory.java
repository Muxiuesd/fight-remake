package ttk.muxiuesd.world.entity.genfactory;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.interfaces.world.entity.EntityGenFactory;
import ttk.muxiuesd.registrant.Gets;
import ttk.muxiuesd.registry.Entities;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.item.ItemStack;

public class ItemEntityGenFactory implements EntityGenFactory<ItemEntity> {
    public static ItemEntity create (EntitySystem entitySystem, Vector2 position, ItemStack itemStack) {
        ItemEntity entity = (ItemEntity) Gets.ENTITY(Entities.ITEM_ENTITY, entitySystem);
        entity.setPosition(position);
        entity.setItemStack(itemStack);
        return entity;
    }
}
