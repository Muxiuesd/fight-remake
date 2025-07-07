package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.interfaces.world.entity.BulletFactory;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.registry.ItemStackBehaviours;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.GroundEntitySystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Bullet;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Weapon;

/**
 * 远程武器，可发射子弹
 * */
public class RangedWeapon extends Weapon {
    /**
     * 新建一个默认的远程武器的属性
     */
    public static Property createDefaultProperty() {
        return new Property().setPropertiesMap(DEFAULT_WEAPON_PROPERTIES_DATA_MAP.copy())
            .setUseSoundId(Sounds.ENTITY_SHOOT.getId());
    }

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
            Bullet bullet = this.factory.create(world, user, user.getDirection());
            GroundEntitySystem entitySystem = user.getEntitySystem();
            entitySystem.add(bullet);

            return super.use(itemStack, world, user);
        }
        return false;
    }

    /**
     * 获取子弹实体工厂
     * */
    public BulletFactory<?> getFactory () {
        return this.factory;
    }

    /**
     * 设置子弹实体工厂
     * */
    public RangedWeapon setFactory (BulletFactory<?> factory) {
        this.factory = factory;
        return this;
    }

    @Override
    public IItemStackBehaviour getBehaviour () {
        return ItemStackBehaviours.RANGED_WEAPON;
    }
}
