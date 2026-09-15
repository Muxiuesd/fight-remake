package ttk.muxiuesd.world.item.consumption;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.abs.StatusEffect;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 效果消费物品（食物 / 药水的统一模板）
 * <p>
 * 使用后向使用者施加一组状态效果，并播放音效。
 * 合并自原先重复的 {@code FoodItem}/{@code PotionItem}。
 */
public class EffectItem extends ConsumptionItem {
    /// 默认的食用音效
    public static final AudioHolder[] EAT_SOUNDS = new AudioHolder[] {
        Sounds.ENTITY_EAT_1, Sounds.ENTITY_EAT_2, Sounds.ENTITY_EAT_3
    };

    private Effect[] effects;            //使用后获得的状态效果
    private AudioHolder[] consumeSounds; //可选的使用音效（null 时使用属性默认音效）

    public EffectItem (Property property) {
        super(property);
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        //播放音效：指定了音效数组就随机播放，否则用属性默认音效
        AudioHolder sound;
        if (this.consumeSounds != null && this.consumeSounds.length > 0) {
            sound = this.consumeSounds[MathUtils.random(0, this.consumeSounds.length - 1)];
        } else {
            sound = this.getProperty().getUseSound();
        }
        world.getSystem(SoundSystem.class).playSpatialSound(sound, user);

        //把效果施加到 user
        if (this.effects != null) {
            for (EffectItem.Effect effect : this.effects) {
                user.setEffect(effect.getEffect(), effect.getDuration(), effect.getLevel());
            }
        }
        return true;
    }

    public EffectItem.Effect[] getEffects () {
        return this.effects;
    }

    public EffectItem setEffects (EffectItem.Effect[] effects) {
        this.effects = effects;
        return this;
    }

    public AudioHolder[] getConsumeSounds () {
        return this.consumeSounds;
    }

    public EffectItem setConsumeSounds (AudioHolder[] consumeSounds) {
        this.consumeSounds = consumeSounds;
        return this;
    }

    /**
     * 使用后会获得的状态效果
     * <p>
     * 内部通过 {@link StatusEffect.Data} 组合存储 duration 和 level，消除字段重复。
     * Effect 本身是不可变的规格对象，只暴露 getter 不暴露 Data 实例。
     */
    public static class Effect {
        private final StatusEffect effect;
        private final StatusEffect.Data data;

        private Effect (StatusEffect effect, float duration, int level) {
            this.effect = effect;
            this.data = new StatusEffect.Data(duration, level);
        }

        public static Effect of (StatusEffect effect, float duration, int level) {
            return new Effect(effect, duration, level);
        }

        public StatusEffect getEffect () {
            return this.effect;
        }

        public float getDuration () {
            return this.data.getDuration();
        }

        public int getLevel () {
            return this.data.getLevel();
        }
    }
}