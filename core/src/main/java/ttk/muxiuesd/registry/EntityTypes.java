package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.common.EntityFishingHook;


/**
 * 所有的实体类型
 * */
public final class EntityTypes {
    public static void init () {
    }

    public static final EntityType<Player> PLAYER = register("player", new EntityType<Player>());
    public static final EntityType<Bullet> PLAYER_BULLET = register("player_bullet",
        PLAYER.addChildType("bullet", new EntityType<Bullet>()));
    public static final EntityType<EntityFishingHook> PLAYER_FIASHING_HOOK = register("player_fishing_hook",
        PLAYER.addChildType("fishing_hook", new EntityType<EntityFishingHook>()));

    public static final EntityType<Enemy<?>> ENEMY = register("enemy", new EntityType<Enemy<?>>());
    public static final EntityType<Bullet> ENEMY_BULLET = register("enemy_bullet",
        ENEMY.addChildType("bullet", new EntityType<Bullet>()));

    public static final EntityType<LivingEntity<?>> CREATURE = register("creature", new EntityType<LivingEntity<?>>());
    public static final EntityType<ItemEntity> ITEM_ENTITY = register("item_entity", new EntityType<ItemEntity>());


    public static <T extends Entity<?>> EntityType<T> register (String name, EntityType<T> entityType) {
        Identifier identifier = new Identifier(Fight.ID(name));
        return (EntityType<T>) Registries.ENTITY_TYPE.register(identifier, entityType.setId(identifier.getId()));
    }
}
