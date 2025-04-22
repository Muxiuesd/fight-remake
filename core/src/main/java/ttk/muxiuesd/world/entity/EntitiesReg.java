package ttk.muxiuesd.world.entity;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.bullet.BulletFire;
import ttk.muxiuesd.world.entity.enemy.Slime;

import java.util.function.Supplier;

/**
 * 实体注册
 * TODO mod注册实体
 * */
public class EntitiesReg {
    public static final String TAG = EntitiesReg.class.getName();
    public static final Registrant<Entity> registrant = RegistrantGroup.getRegistrant(Fight.NAMESPACE, Entity.class);

    public static void registerAllEntities () {
        Log.print(TAG, "游戏实体注册完毕");
    }

    public static final Entity ITEM_ENTITY = register("item_entity", ItemEntity::new);
    public static final Entity PLAYER = register("player", Player::new);
    public static final Entity SLIME = register("slime", Slime::new);
    public static final Entity BULLET_FIRE = register("bullet_fire", BulletFire::new);

    public static Entity register (String name, Supplier<? extends Entity> supplier) {
        return registrant.register(name, supplier);
    }

    public static Entity get (String name) {
        return registrant.get(name);
    }
}
