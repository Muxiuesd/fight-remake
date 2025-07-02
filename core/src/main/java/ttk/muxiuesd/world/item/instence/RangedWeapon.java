package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.interfaces.world.entity.BulletFactory;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Weapon;

/**
 * 远程武器，可发射子弹
 * */
public class RangedWeapon extends Weapon {
    private BulletFactory<?> factory;   //子弹的工厂实现类

    public RangedWeapon (Property property, String textureId, String texturePath) {
        super(property, textureId, texturePath);
    }

    public RangedWeapon (Property property, String textureId, String texturePath, BulletFactory<?> factory) {
        super(property, textureId, texturePath);
        this.factory = factory;
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity user) {
        if (this.getFactory() != null) {
            //生成子弹
            Bullet bullet = factory.create(world, user, user.getDirection());
            EntitySystem entitySystem = user.getEntitySystem();
            entitySystem.add(bullet);
        }
        return super.use(itemStack, world, user);
    }

    public BulletFactory<?> getFactory () {
        return this.factory;
    }

    public RangedWeapon setFactory (BulletFactory<?> factory) {
        this.factory = factory;
        return this;
    }
}
