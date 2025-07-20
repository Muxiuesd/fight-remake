package ttk.muxiuesd.registry;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.id.Identifier;
import ttk.muxiuesd.registrant.Registries;
import ttk.muxiuesd.world.entity.ItemEntity;
import ttk.muxiuesd.world.entity.Player;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.bullet.BulletFire;
import ttk.muxiuesd.world.entity.common.EntityFishingHook;
import ttk.muxiuesd.world.entity.creature.PufferFish;
import ttk.muxiuesd.world.entity.enemy.EntityTarget;
import ttk.muxiuesd.world.entity.enemy.Slime;

import java.util.function.Supplier;

public final class Entities {
    public static void init () {
    }

    public static final ItemEntity ITEM_ENTITY = register("item_entity", ItemEntity::new);
    //活物实体
    public static final Player PLAYER = register("player", Player::new);

    //敌人
    public static final Slime SLIME = register("slime", Slime::new);
    public static final EntityTarget TARGET = register("target", EntityTarget::new);

    //生物
    public static final PufferFish PUFFER_FISH = register("puffer_fish", PufferFish::new);

    //子弹实体
    public static final BulletFire BULLET_FIRE = register("bullet_fire", BulletFire::new);

    //最普通的实体
    public static final EntityFishingHook FISHING_HOOK = register("fishing_hook", EntityFishingHook::new);


    public static <T extends Entity<?>> T register (String name, Supplier<T> supplier) {
        Identifier identifier = new Identifier(Fight.NAMESPACE, name);
        Registries.ENTITY.register(identifier, supplier);
        return (T) supplier.get().setID(identifier.getId());
    }
}
