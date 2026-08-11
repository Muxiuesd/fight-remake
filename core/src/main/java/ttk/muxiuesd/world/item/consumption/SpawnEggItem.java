package ttk.muxiuesd.world.item.consumption;

import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.player.Player;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 用来召唤实体的物品（刷怪蛋）
 * */
public class SpawnEggItem<T extends Entity<T>> extends ConsumptionItem{
    private final EntityProvider<T> entityProvider;

    public SpawnEggItem (EntityProvider<T> entityProvider) {
        super();
        this.entityProvider = entityProvider;
    }
    public SpawnEggItem (Property property, EntityProvider<T> entityProvider) {
        super(property);
        this.entityProvider = entityProvider;
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        if (user instanceof Player) {
            EntitySystem es = world.getSystem(EntitySystem.class);
            T entity = this.entityProvider.create(world);
            entity.setPosition(Util.getMouseWorldPosition());
            entity.setEntitySystem(es);
            es.add(entity);
        }

        return super.use(itemStack, world, user);
    }

    public EntityProvider<T> getEntityProvider () {
        return this.entityProvider;
    }
}
