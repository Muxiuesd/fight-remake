package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Factory;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.abs.Weapon;

/**
 * 测试武器
 * */
public class WeaponTest extends Weapon {
    public WeaponTest () {
        super(new WeaponProperties().setDamage(7f).setUseSpan(0.8f).setDuration(500),
            Fight.getId("test_weapon"),
            Fight.getItemTexture("weapon.png"));
    }

    @Override
    public boolean use (World world, LivingEntity user) {
        EntitySystem entitySystem = user.getEntitySystem();
        entitySystem.add(Factory.createBullet(user, Util.getDirection()));

        return super.use(world, user);
    }
}
