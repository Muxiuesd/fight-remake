package ttk.muxiuesd.world.entity.common;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.abs.Entity;

/**
 * 鱼钩实体
 * */
public class EntityFishingHook extends Entity {
    public EntityFishingHook() {
        initialize(Group.player);
        setSpeed(0);
        setSize(0.7f, 0.7f);
        bodyTexture = getTextureRegion(Fight.getId("fishing_hook"), "fish/fishing_hook.png");
    }
}
