package ttk.muxiuesd.world.entity;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.world.entity.enemy.Slime;

public class Entities {
    static Registrant<Entity> registrant = RegistrantGroup.getRegistrant(Fight.NAMESPACE, Entity.class);
    static {
        register("slime", new Slime());
    }
    private static void register (String id, Entity entity) {
        registrant.register(id, entity);
        return;
    }
}
