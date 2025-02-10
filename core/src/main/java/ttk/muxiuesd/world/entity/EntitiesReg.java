package ttk.muxiuesd.world.entity;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.world.entity.enemy.Slime;

/**
 * 实体注册
 * TODO mod注册实体
 * */
public class EntitiesReg {
    static Registrant<Entity> registrant = RegistrantGroup.getRegistrant(Fight.NAMESPACE, Entity.class);
    static {
        //register("slime", new Slime());
    }

    public static void registerAllEntities () {
        register("player", new Player());
        register("slime", new Slime());
    }

    private static void register (String id, Entity entity) {
        registrant.register(id, entity);
        return;
    }

    public static Entity get (String id) {
        return registrant.get(id);
    }

    public static <T> T get (String id, Class<T> clazz) {
        return (T) get(id);
    }
}
