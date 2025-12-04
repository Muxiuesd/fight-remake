package ttk.muxiuesd.world.entity.enemy;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.EntityTypes;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.Enemy;

/**
 * 靶子，不会动，不会攻击
 * */
public class EntityTarget extends Enemy<EntityTarget> {
    public EntityTarget (World world, EntityType<?> entityType) {
        super(world, EntityTypes.ENEMY, 1000, 1000, 1, 1, 1, 0);
        textureRegion = getTextureRegion(Fight.ID("fish"), "fish/fish.png");
    }

    @Override
    public void remoteAttack (float delta, EntitySystem es) {
        //super.attack(delta, es);
    }
}
