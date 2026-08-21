package ttk.muxiuesd.world.item.consumption.potion;

import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.world.item.abs.Item;

import java.util.function.Function;

/**
 * 药水物品的构建器
 * */
public class PotionItemBuilder {
    private AudioHolder drinkSound = Sounds.ENTITY_DRINK;    //喝下去的音效
    private PotionItem.DrinkEffect[] drinkEffects;           //喝下去的状态效果


    private PotionItemBuilder () {}

    public static PotionItemBuilder create () {
        return new PotionItemBuilder();
    }

    /**
     * 构建药水物品
     * */
    public Function<Item.Property, Item> build () {
        return (property) -> {
            property.setUseSound(this.drinkSound);

            return new PotionItem(property)
                .setDrinkEffects(this.drinkEffects);
        };
    }

    public PotionItemBuilder setDrinkSound (AudioHolder drinkSound) {
        this.drinkSound = drinkSound;
        return this;
    }

    public PotionItemBuilder setDrinkEffects (PotionItem.DrinkEffect... drinkEffects) {
        this.drinkEffects = drinkEffects;
        return this;
    }
}
