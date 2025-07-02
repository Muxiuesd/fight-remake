package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityFactory;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.bullet.BulletFire;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.RangedWeapon;
import ttk.muxiuesd.world.item.abs.Weapon;

/**
 * 测试武器
 * */
public class WeaponDiamondSword extends RangedWeapon {
    public WeaponDiamondSword () {
        super(Weapon.createDefaultProperty(),
            Fight.getId("test_weapon"),
            Fight.ItemTexturePath("diamond_sword.png"),
            (world, owner, direction) -> {
                BulletFire bullet = new BulletFire(owner);
                bullet.setPosition(owner.x + (owner.width - bullet.width) / 2, owner.y + (owner.height - bullet.height) / 2);
                bullet.setDirection(direction.getxDirection(), direction.getyDirection());
                bullet.setCullingArea(bullet.x, bullet.y, bullet.width, bullet.height);
                return bullet;
            });
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity user) {
        EntitySystem entitySystem = user.getEntitySystem();
        entitySystem.add(EntityFactory.createFireBullet(user, Util.getDirection()));

        return super.use(itemStack, world, user);
    }
}
