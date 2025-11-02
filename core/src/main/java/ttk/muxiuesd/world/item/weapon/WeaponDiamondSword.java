package ttk.muxiuesd.world.item.weapon;

import ttk.muxiuesd.Fight;
import ttk.muxiuesd.interfaces.world.entity.BulletFactory;
import ttk.muxiuesd.registry.Entities;
import ttk.muxiuesd.util.Direction;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.Entity;
import ttk.muxiuesd.world.entity.bullet.BulletFire;

/**
 * 测试武器
 * */
public class WeaponDiamondSword extends RangedWeapon {
    public WeaponDiamondSword () {
        super(RangedWeapon.createDefaultProperty(),
            Fight.ID("diamond_sword"),
            Fight.ItemTexturePath("diamond_sword.png"),
            new BulletFactory<BulletFire>() {
                @Override
                public BulletFire create (World world, Entity<?> owner, EntityType<?> entityType, Direction direction) {
                    BulletFire bullet = Entities.BULLET_FIRE.create(world);
                    bullet.setOwner(owner);
                    bullet.setType(owner.getType().getChildType("bullet"));
                    bullet.setPosition(owner.x + (owner.width - bullet.width) / 2, owner.y + (owner.height - bullet.height) / 2);
                    bullet.setDirection(direction.getxDirection(), direction.getyDirection());
                    bullet.setCullingArea(bullet.x, bullet.y, bullet.width, bullet.height);
                    return bullet;
                }
                @Override
                public String getBulletId () {
                    return Entities.BULLET_FIRE.getId();
                }
            }
        );
    }
}
