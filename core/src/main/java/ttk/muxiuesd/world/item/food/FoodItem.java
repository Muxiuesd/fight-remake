package ttk.muxiuesd.world.item.food;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.consumption.ConsumptionItem;

/**
 * 食物类型的物品
 * TODO 食物物品的特有属性数据
 * */
public class FoodItem extends ConsumptionItem {
    /**
     * 默认属性，默认的吃食物的音效
     * */
    public static Property createDefaultProperty() {
        return new Property()
            .setMaxCount(64);
    }


    public FoodItem (String textureId) {
        super(createDefaultProperty(), textureId);
    }

    public FoodItem (String textureId, String texturePath) {
        super(createDefaultProperty(), textureId, texturePath);
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        //随机吃食物音效
        AudioHolder eatSound = Sounds.ENTITY_EAT_1;
        int random = MathUtils.random(1, 3);
        switch (random) {
            case 2: {
                eatSound = Sounds.ENTITY_EAT_2;
                break;
            }
            case 3: {
                eatSound = Sounds.ENTITY_EAT_3;
                break;
            }
        }
        SoundSystem ses = world.getSystem(SoundSystem.class);
        ses.playSpatialSound(eatSound, user);
        return true;
    }
}
