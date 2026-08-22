package ttk.muxiuesd.world.entity.enemy;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.Enemy;

/**
 * 僵尸敌人
 * <p>
 * 接近敌人进行近战攻击
 * */
public class Zombie extends Enemy<Zombie> {
    public Zombie (World world, EntityType<?> entityType) {
        super(world, entityType, 20, 20, 16, 1f, 1.5f, 20);
    }
}
