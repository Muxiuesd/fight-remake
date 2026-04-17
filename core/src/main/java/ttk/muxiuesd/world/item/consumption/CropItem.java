package ttk.muxiuesd.world.item.consumption;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 农作物类型的物品
 * <p>
 * 对着耕地使用可以种植，或者可以直接食用
 * */
public class CropItem extends ConsumptionItem{

    public CropItem (String name) {
        super(name);
    }

    public CropItem (Property property, String textureId) {
        super(property, textureId);
    }

    public CropItem (Property property, String textureId, String texturePath) {
        super(property, textureId, texturePath);
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        //TODO 对着耕地使用可以种植

        return super.use(itemStack, world, user);
    }
}
