package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.world.entity.bullet.BulletFire;

/**
 * 测试武器
 * */
public class WeaponDiamondSword extends RangedWeapon {
    public WeaponDiamondSword () {
        super(RangedWeapon.createDefaultProperty(),
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
}
