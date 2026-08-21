package ttk.muxiuesd.world.item.consumption.food;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.abs.StatusEffect;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.consumption.ConsumptionItem;

/**
 * 食物类型的物品
 * */
public class FoodItem extends ConsumptionItem {
    /// 默认的吃食物音效
    public static final AudioHolder[] EAT_SOUNDS = new AudioHolder[] {
        Sounds.ENTITY_EAT_1, Sounds.ENTITY_EAT_2, Sounds.ENTITY_EAT_3
    };


    private AudioHolder[] eatSounds;    //吃这个食物的音效
    private EatEffect[] eatEffects;     //吃这个食物会获得的状态效果

    public FoodItem (Property property) {
        super(property);
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        //随机播放吃食物音效
        AudioHolder[] sounds = this.getEatSounds();
        if (sounds != null && sounds.length != 0) {
            int random = MathUtils.random(0, sounds.length - 1);

            SoundSystem ses = world.getSystem(SoundSystem.class);
            ses.playSpatialSound(sounds[random], user);
        }

        //把效果应用与user
        EatEffect[] effects = this.getEatEffects();
        if (effects != null) {
            for (EatEffect effect : effects) {
                user.setEffect(effect.getEffect(), effect.getDuration(), effect.getLevel());
            }
        }

        return true;
    }

    public AudioHolder[] getEatSounds () {
        return this.eatSounds;
    }

    public FoodItem setEatSounds (AudioHolder[] eatSounds) {
        this.eatSounds = eatSounds;
        return this;
    }

    public EatEffect[] getEatEffects () {
        return eatEffects;
    }

    public FoodItem setEatEffects (EatEffect[] eatEffects) {
        this.eatEffects = eatEffects;
        return this;
    }

    /**
     * 食物吃下去会给什么状态效果
     * */
    public static class EatEffect {
        private final StatusEffect effect;
        private final float duration;       //持续时间，单位：秒
        private final int level;            //状态效果等级

        private EatEffect (StatusEffect effect, float duration, int level) {
            this.effect = effect;
            this.duration = duration;
            this.level = level;
        }

        /**
         * 创建一个
         * */
        public static EatEffect of (StatusEffect effect, float duration, int level) {
            return new EatEffect(effect, duration, level);
        }

        public StatusEffect getEffect () {
            return this.effect;
        }

        public float getDuration () {
            return this.duration;
        }

        public int getLevel () {
            return this.level;
        }
    }
}
