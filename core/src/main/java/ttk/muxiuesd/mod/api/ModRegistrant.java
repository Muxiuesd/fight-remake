package ttk.muxiuesd.mod.api;

import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.world.block.Block;
import ttk.muxiuesd.world.entity.Entity;

/**
 * mod专用：获取注册器
 * */
public class ModRegistrant {

    public Registrant<Block> getBlockRegister(String namespace) {
        return RegistrantGroup.getRegistrant(namespace, Block.class);
    }

    public static Registrant<Entity> getEntityRegister(String namespace) {
        return RegistrantGroup.getRegistrant(namespace, Entity.class);
    }
}
