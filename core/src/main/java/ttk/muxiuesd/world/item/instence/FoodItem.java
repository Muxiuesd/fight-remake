package ttk.muxiuesd.world.item.instence;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.util.Log;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 食物类型的物品
 * */
public class FoodItem extends ConsumptionItem {
    /**
     * 默认属性，默认的吃食物的音效
     * */
    public static Property createDefaultProperty() {
        return new Property().setMaxCount(64).setUseSoundId(Fight.getId("eat_"));
    }


    public FoodItem (String textureId) {
        super(createDefaultProperty(), textureId);
    }

    public FoodItem (String textureId, String texturePath) {
        super(createDefaultProperty(), textureId, texturePath);
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        Log.print(this.toString(), user.getID() + " 在吃鱼");
        //随机吃鱼音效
        String useSoundId = this.property.getUseSoundId() + MathUtils.random(1, 3);
        SoundEffectSystem ses = world.getSystem(SoundEffectSystem.class);
        ses.newSpatialSound(useSoundId, user);
        return true;
    }
}
