package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityFactory;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Weapon;

/**
 * 测试武器
 * */
public class WeaponDiamondSword extends Weapon {
    public WeaponDiamondSword () {
        super(Weapon.createDefaultProperty(),
            Fight.getId("test_weapon"),
            Fight.ItemTexturePath("diamond_sword.png"));
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity user) {
        EntitySystem entitySystem = user.getEntitySystem();
        entitySystem.add(EntityFactory.createFireBullet(user, Util.getDirection()));

        return super.use(itemStack, world, user);
    }
}
