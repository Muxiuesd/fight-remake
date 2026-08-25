package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.interfaces.world.entity.EntityProvider;
import ttk.muxiuesd.registrant.EntityRendererRegistry;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.render.world.entity.*;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.bullet.BulletFire;
import ttk.muxiuesd.world.entity.common.EntityFishingHook;
import ttk.muxiuesd.world.entity.creature.PufferFish;
import ttk.muxiuesd.world.entity.enemy.EntityTarget;
import ttk.muxiuesd.world.entity.enemy.Slime;
import ttk.muxiuesd.world.entity.enemy.Zombie;
import ttk.muxiuesd.world.entity.player.Player;

/**
 * 游戏内所有实体的注册
 * <p>
 * 实体的注册是以它的Provider形式注册，相当于注册了它的工厂，工厂内的属性在注册阶段写定
 * */
public final class Entities {
    public static void init () {}

    public static final EntityProvider<ItemEntity> ITEM_ENTITY = register(
        "item_entity",
        EntityProvider.Builder.<ItemEntity>create(ItemEntity::new)
            .setDefaultType(EntityTypes.ITEM_ENTITY)
            .setRenderer(ItemEntityRenderer::new)
            .setCodec(ItemEntity.CODEC)
            .build()
    );
    //活物实体
    public static final EntityProvider<Player> PLAYER = register(
        "player",
        EntityProvider.Builder.<Player>create(Player::new)
            .setDefaultType(EntityTypes.PLAYER)
            .setRenderer(PlayerRenderer::new)
            .setCodec(Player.CODEC)
            .build()
    );

    //敌人
    public static final EntityProvider<Slime> SLIME = register(
        "slime",
        EntityProvider.Builder.<Slime>create(Slime::new)
            .setDefaultType(EntityTypes.ENEMY)
            .setRenderer(() -> new EnemyRenderer<>(Fight.ID("slime"), "enemy/slime.png"))
            .setCodec(Slime.CODEC)
            .build()
    );

    public static final EntityProvider<EntityTarget> TARGET = register(
        "target",
        EntityProvider.Builder.<EntityTarget>create(EntityTarget::new)
            .setDefaultType(EntityTypes.ENEMY)
            .setRenderer(() -> new EnemyRenderer<>(Fight.ID("fish"), "fish/fish.png"))
            .setCodec(LivingEntity.CODEC)
            .build()
    );

    public static final EntityProvider<Zombie> ZOMBIE = register(
        "zombie",
        EntityProvider.Builder.<Zombie>create(Zombie::new)
            .setDefaultType(EntityTypes.ENEMY)
            .setRenderer(() -> new EnemyRenderer<>(Fight.ID("zombie"), "enemy/zombie.png"))
            .setCodec(LivingEntity.CODEC)
            .build()
    );

    //生物
    public static final EntityProvider<PufferFish> PUFFER_FISH = register(
        "puffer_fish",
        EntityProvider.Builder.<PufferFish>create(PufferFish::new)
            .setDefaultType(EntityTypes.CREATURE)
            .setRenderer(() -> new LivingEntityRenderer<>(Fight.ID("puffer_fish"), "fish/puffer_fish.png"))
            .setCodec(LivingEntity.CODEC)
            .build()
    );

    //子弹实体
    public static final EntityProvider<BulletFire> BULLET_FIRE = register(
        "bullet_fire",
        EntityProvider.Builder.<BulletFire>create(BulletFire::new)
            .setDefaultType(EntityTypes.ENEMY_BULLET)
            .setRenderer(BulletRenderer::new)
            .setCodec(Bullet.CODEC)
            .build()
    );

    //最普通的实体
    public static final EntityProvider<EntityFishingHook> FISHING_HOOK = register(
        "fishing_hook",
        EntityProvider.Builder.<EntityFishingHook>create(EntityFishingHook::new)
            .setDefaultType(EntityTypes.PLAYER_FIASHING_HOOK)
            .setRenderer(FishingHookRenderer::new)
            .setCodec(Entity.CODEC)
            .setCanBeSaved(false)
            .build()
    );

    /**
     * 最基础的实体注册
     * */
    public static <T extends Entity<T>> EntityProvider<T> register (String name, EntityProvider<T> provider) {
        Identifier identifier = Identifier.of(Fight.NAMESPACE, name);
        Registries.ENTITY.register(identifier, provider.setIdentifier(identifier));
        EntityRendererRegistry.register(provider, provider.renderer);
        return provider;
    }
}
