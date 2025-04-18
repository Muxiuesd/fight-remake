package ttk.muxiuesd.registrant;

import ttk.muxiuesd.interfaces.ID;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 游戏注册元素获得类
 * */
public class Gets {

    public static Item ITEM (String id) {
        return get(id, Item.class);
    }

    public static Block BLOCK (String id) {
        return get(id, Block.class);
    }

    public static Entity ENTITY (String id) {
        return get(id, Entity.class);
    }

    public static <C extends ID> C get (String id, Class<C> clazz) {
        String[] split = id.split(":");
        Registrant<C> registrant = RegistrantGroup.getRegistrant(split[0], clazz);
        return registrant.get(split[1]);
    }
}

