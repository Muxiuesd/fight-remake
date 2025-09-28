package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Entities;
import ttk.muxiuesd.world.entity.bullet.BulletFire;

/**
 * 测试武器
 * */
public class WeaponDiamondSword extends RangedWeapon {
    public WeaponDiamondSword () {
        super(RangedWeapon.createDefaultProperty(),
            Fight.ID("test_weapon"),
            Fight.ItemTexturePath("diamond_sword.png"),
            (world, owner, entityType, direction) -> {
                BulletFire bullet = Entities.BULLET_FIRE.create(world);
                bullet.setOwner(owner);
                bullet.setType(owner.getType().getChildType("bullet"));
                bullet.setPosition(owner.x + (owner.width - bullet.width) / 2, owner.y + (owner.height - bullet.height) / 2);
                bullet.setDirection(direction.getxDirection(), direction.getyDirection());
                bullet.setCullingArea(bullet.x, bullet.y, bullet.width, bullet.height);
                return bullet;
            });
    }
}
