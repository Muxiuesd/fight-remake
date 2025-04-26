package ttk.muxiuesd.world.entity.creature;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

/**
 * 生物：鱼
 * <p>
 * 生成在水里，会被饵料吸引，会被钓
 * */
public class EntityFish extends LivingEntity {
    public EntityFish () {
        initialize(Group.creature, 5, 5, 2);
        setSpeed(0);
        setSize(0.4f, 0.4f);
        bodyTexture = getTextureRegion(Fight.getId("fish"), "fish/fish.png");
    }
}
