package ttk.muxiuesd.world.entity.creature;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.GroundEntitySystem;
import ttk.muxiuesd.world.entity.abs.Enemy;

/**
 * 靶子，不会动，不会攻击
 * */
public class EntityTarget extends Enemy {
    public EntityTarget () {
        super(1000, 1000, 1, 1, 1, 0);
        bodyTexture = getTextureRegion(Fight.getId("fish"), "fish/fish.png");
    }

    @Override
    public void attack (float delta, GroundEntitySystem es) {
        //super.attack(delta, es);
    }
}
