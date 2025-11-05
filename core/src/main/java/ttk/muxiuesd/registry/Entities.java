package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.bullet.BulletFire;
import ttk.muxiuesd.world.entity.common.EntityFishingHook;
import ttk.muxiuesd.world.entity.creature.PufferFish;
import ttk.muxiuesd.world.entity.enemy.EntityTarget;
import ttk.muxiuesd.world.entity.enemy.Slime;

/**
 * 所有实体的注册
 * */
public final class Entities {
    public static void init () {
    }

    public static final EntityProvider<ItemEntity> ITEM_ENTITY = register(
        "item_entity",
        EntityProvider.Builder.<ItemEntity>create(ItemEntity::new)
            .setDefaultType(EntityTypes.ITEM_ENTITY)
            .setCodec(Codecs.ITEM_ENTITY)
            .build()
    );
    //活物实体
    public static final EntityProvider<Player> PLAYER = register(
        "player",
        EntityProvider.Builder.<Player>create(Player::new)
            .setDefaultType(EntityTypes.PLAYER)
            .setCodec(Codecs.PLAYER)
            .build()
    );

    //敌人
    public static final EntityProvider<Slime> SLIME = register(
        "slime",
        EntityProvider.Builder.<Slime>create(Slime::new)
            .setDefaultType(EntityTypes.ENEMY)
            .setCodec(Codecs.LIVING_ENTITY)
            .build()
    );

    public static final EntityProvider<EntityTarget> TARGET = register(
        "target",
        EntityProvider.Builder.<EntityTarget>create(EntityTarget::new)
            .setDefaultType(EntityTypes.ENEMY)
            .setCodec(Codecs.LIVING_ENTITY)
            .build()
    );

    //生物
    public static final EntityProvider<PufferFish> PUFFER_FISH = register(
        "puffer_fish",
        EntityProvider.Builder.<PufferFish>create(PufferFish::new)
            .setDefaultType(EntityTypes.CREATURE)
            .setCodec(Codecs.LIVING_ENTITY)
            .build()
    );

    //子弹实体
    public static final EntityProvider<BulletFire> BULLET_FIRE = register(
        "bullet_fire",
        EntityProvider.Builder.<BulletFire>create(BulletFire::new)
            .setDefaultType(EntityTypes.ENEMY_BULLET)
            .setCodec(Codecs.ENTITY)
            .build()
    );

    //最普通的实体
    public static final EntityProvider<EntityFishingHook> FISHING_HOOK = register(
        "fishing_hook",
        EntityProvider.Builder.<EntityFishingHook>create(EntityFishingHook::new)
            .setDefaultType(EntityTypes.PLAYER_FIASHING_HOOK)
            .setCodec(Codecs.ENTITY)
            .setCanBeSaved(false)
            .build()
    );


    public static <T extends Entity<?>> EntityProvider<T> register (String name, EntityProvider<T> provider) {
        Identifier identifier = new Identifier(Fight.NAMESPACE, name);
        return (EntityProvider<T>) Registries.ENTITY.register(identifier, provider.setId(identifier.getId()));
    }
}
