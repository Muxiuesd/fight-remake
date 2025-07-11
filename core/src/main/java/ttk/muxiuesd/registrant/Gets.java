package ttk.muxiuesd.registrant;

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

    public static Entity ENTITY (Entity entity, EntitySystem entitySystem) {
        return ENTITY(entity.getID(), entitySystem);
    }
    /**
     * 已知实体系统获取新实体，自动设置所属的实体系统，自动添加进去
     * */
    public static Entity ENTITY (String id, EntitySystem entitySystem) {
        Entity entity = Registries.ENTITY.get(id).get().setID(id).setEntitySystem(entitySystem);
        entitySystem.add(entity);
        return entity;
    }

    public static Entity ENTITY (String id) {
        return Registries.ENTITY.get(id).get().setID(id);
    }

    public static Enemy ENEMY (String id) {
        return (Enemy) ENTITY(id);
    }

    public static Enemy ENEMY (String id, EntitySystem entitySystem) {
        return (Enemy) ENTITY(id, entitySystem);
    }

    public static Bullet BULLET (String id) {
        return (Bullet) ENTITY(id);
    }

    /*public static <C extends ID> C get (String id, Class<C> clazz) {
        String[] split = id.split(":");
        Registrant<C> registrant = RegistrantGroup.getRegistrant(split[0], clazz);
        return registrant.get(split[1]);
    }*/
}

