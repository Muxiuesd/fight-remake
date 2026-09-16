package ttk.muxiuesd.world.item.consumption;

import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.builder.ItemBuilder;

/**
 * 效果消费物品（食物 / 药水）的构建器
 * <p>
 * 合并自原先重复的 {@code FoodItemBuilder}/{@code PotionItemBuilder}。
 */
public class EffectItemBuilder implements ItemBuilder<EffectItem> {
    private AudioHolder[] consumeSounds;          //可选的使用音效（null 走属性默认音效）
    private EffectItem.Effect[] effects;          //使用后获得的状态效果
    private Item.Property property = new Item.Property();    //基础属性

    private EffectItemBuilder () {
    }

    public static EffectItemBuilder create () {
        return new EffectItemBuilder();
    }

    @Override
    public EffectItem build () {
        return new EffectItem(this.property.copy())
            .setConsumeSounds(this.consumeSounds)
            .setEffects(this.effects);
    }

    public EffectItemBuilder setSounds (AudioHolder... consumeSounds) {
        this.consumeSounds = consumeSounds;
        return this;
    }

    public EffectItemBuilder setEffects (EffectItem.Effect... effects) {
        this.effects = effects;
        return this;
    }

    public EffectItemBuilder setProperty (Item.Property property) {
        if (property != null) this.property = property;
        return this;
    }

    public EffectItemBuilder setMaxCount (int maxCount) {
        this.property.setMaxCount(maxCount);
        return this;
    }
}