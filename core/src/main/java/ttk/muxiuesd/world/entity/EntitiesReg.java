package ttk.muxiuesd.world.entity;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.entity.enemy.Slime;

import java.util.function.Supplier;

/**
 * 实体注册
 * TODO mod注册实体
 * */
public class EntitiesReg {
    public static final String TAG = EntitiesReg.class.getName();
    static Registrant<Entity> registrant = RegistrantGroup.getRegistrant(Fight.NAMESPACE, Entity.class);


    public static void registerAllEntities () {
        Log.print(TAG, "游戏方块注册完毕");
    }

    public static final Entity PLAYER = register("player", Player::new);
    public static final Entity SLIME = register("slime", Slime::new);


    private static Entity register (String name, Supplier<Entity> supplier) {
        return registrant.register(name, supplier);
    }

    public static Entity get (String name) {
        return registrant.get(name);
    }
}
