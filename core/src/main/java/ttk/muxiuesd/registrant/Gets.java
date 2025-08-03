package ttk.muxiuesd.registrant;

import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 游戏注册元素获得类
 * */
public class Gets {

    public static Item ITEM (String id) {
        return Registries.ITEM.get(id);
    }

    public static Block BLOCK (String id) {
        return Registries.BLOCK.get(id);
    }

    public static Entity<?> ENTITY (EntityProvider<?> provider, EntitySystem entitySystem) {
        Entity<?> entity = provider.create(entitySystem.getWorld());
        entity.setEntitySystem(entitySystem);
        entitySystem.add(entity);
        return entity;
    }

    /**
     * 已知实体系统获取新实体，自动设置所属的实体系统，自动添加进去
     * */
    public static Entity<?> ENTITY (String id, EntitySystem entitySystem) {
        EntityProvider<?> entityProvider = Registries.ENTITY.get(id);
        Entity<?> entity = entityProvider.create(entitySystem.getWorld());
        entitySystem.add(entity);
        return entity;
    }

    public static Enemy<?> ENEMY (String id, EntitySystem entitySystem) {
        return (Enemy<?>) ENTITY(id, entitySystem);
    }

    public static Bullet BULLET (String id, EntitySystem entitySystem) {
        return (Bullet) ENTITY(id, entitySystem);
    }
}

