package ttk.muxiuesd.mod.api;

import ttk.muxiuesd.registrant.Registrant;
import ttk.muxiuesd.registrant.RegistrantGroup;
import ttk.muxiuesd.world.block.Block;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * mod专用：获取注册器
 * */
public class ModRegistrant {

    public Registrant<Block> getBlockRegister(String namespace) {
        return RegistrantGroup.getRegistrant(namespace, Block.class);
    }

    public Registrant<Entity> getEntityRegister (String namespace) {
        return RegistrantGroup.getRegistrant(namespace, Entity.class);
    }

    public Registrant<Item> getItemRegister (String namespace) {
        return RegistrantGroup.getRegistrant(namespace, Item.class);
    }
}
