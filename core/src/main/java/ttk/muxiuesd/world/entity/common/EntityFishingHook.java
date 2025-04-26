package ttk.muxiuesd.world.entity.common;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

/**
 * 鱼钩实体
 * */
public class EntityFishingHook extends Entity {
    private LivingEntity owner;

    public EntityFishingHook() {
        initialize(Group.player);
        setSpeed(0);
        setSize(0.7f, 0.7f);
        bodyTexture = getTextureRegion(Fight.getId("fishing_hook"), "fish/fishing_hook.png");
    }

    public LivingEntity getOwner () {
        return this.owner;
    }

    public void setOwner (LivingEntity owner) {
        this.owner = owner;
    }

    public void removeSelf () {
        if (getEntitySystem() == null) return;
        getEntitySystem().remove(this);
    }
}
