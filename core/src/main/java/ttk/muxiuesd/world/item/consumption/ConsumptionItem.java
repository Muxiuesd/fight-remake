package ttk.muxiuesd.world.item.consumption;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.registry.ItemStackBehaviours;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 消费品的模板物品类（用一次数量消耗一个）
 * */
public class ConsumptionItem extends Item {
    private AudioHolder[] consumeSounds; //可选的使用音效（null 时使用属性默认音效）

    public ConsumptionItem () {
        super(new Property());
    }
    public ConsumptionItem (Property property) {
        super(property);
    }

    /**
     * 播放音效
     * */
    @Override
    public void playUseSound (World world, LivingEntity<?> user) {
        //播放音效：指定了音效数组就随机播放，否则用属性默认音效
        AudioHolder sound;
        AudioHolder[] consumeSounds = getConsumeSounds();
        if (consumeSounds != null && consumeSounds.length > 0) {
            sound = consumeSounds[MathUtils.random(0, consumeSounds.length - 1)];
        } else {
            sound = this.getProperty().getUseSound();
        }
        world.getSystem(SoundSystem.class).playSpatialSound(sound, user);
    }

    public AudioHolder[] getConsumeSounds () {
        return this.consumeSounds;
    }

    public ConsumptionItem setConsumeSounds (AudioHolder[] consumeSounds) {
        this.consumeSounds = consumeSounds;
        return this;
    }

    @Override
    public IItemStackBehaviour getBehaviour () {
        return ItemStackBehaviours.CONSUMPTION;
    }
}
