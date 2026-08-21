package ttk.muxiuesd.world.item.consumption.food;

import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.world.item.abs.Item;

import java.util.function.Function;

/**
 * 食物物品的构建器
 * */
public class FoodItemBuilder {
    private AudioHolder[] eatSounds = FoodItem.EAT_SOUNDS;    //吃这个食物的音效
    private FoodItem.EatEffect[] eatEffects;     //吃这个食物会获得的状态效果

    private FoodItemBuilder () {
    }

    public static FoodItemBuilder create () {
        return new FoodItemBuilder();
    }

    /**
     * 构建食物物品
     * */
    public Function<Item.Property, Item> build () {
        return (property) -> new FoodItem(property)
            .setEatSounds(this.eatSounds)
            .setEatEffects(this.eatEffects);
    }

    /**
     * 设置吃食物的声音
     * */
    public FoodItemBuilder setEatSounds (AudioHolder... eatSounds) {
        this.eatSounds = eatSounds;
        return this;
    }

    /**
     * 设置吃食物得到的效果
     * */
    public FoodItemBuilder setEatEffects (FoodItem.EatEffect... eatEffects) {
        this.eatEffects = eatEffects;
        return this;
    }
}
