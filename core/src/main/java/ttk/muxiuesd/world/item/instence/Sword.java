package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Weapon;

/**
 * 剑一类的近战武器
 * */
public class Sword extends Weapon {
    /**
     * 新建一个默认的近战武器的属性
     */
    public static Property createDefaultProperty() {
        return new Property().setPropertiesMap(DEFAULT_WEAPON_PROPERTIES_DATA_MAP.copy())
            .add(PropertyTypes.WEAPON_ATTACK_RANGE, 2f);
    }

    public Sword (Property property, String textureId, String texturePath) {
        super(property, textureId, texturePath);
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity user) {
        //TODO 近战武器的攻击逻辑

        return super.use(itemStack, world, user);
    }
}
