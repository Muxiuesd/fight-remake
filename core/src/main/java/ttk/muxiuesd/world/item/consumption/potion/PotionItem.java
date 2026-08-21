package ttk.muxiuesd.world.item.consumption.potion;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.abs.StatusEffect;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.consumption.ConsumptionItem;

/**
 * 药水物品，使用后获得对应的状态效果
 * */
public class PotionItem extends ConsumptionItem {
    private PotionItem.DrinkEffect[] drinkEffects;     //喝下去的状态效果

    public PotionItem (Property property) {
        super(property);
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        //把效果应用与user
        PotionItem.DrinkEffect[] effects = this.getDrinkEffects();
        if (effects != null) {
            for (PotionItem.DrinkEffect effect : effects) {
                user.setEffect(effect.getEffect(), effect.getDuration(), effect.getLevel());
            }
        }

        return super.use(itemStack, world, user);
    }

    public DrinkEffect[] getDrinkEffects () {
        return drinkEffects;
    }

    public PotionItem setDrinkEffects (DrinkEffect[] drinkEffects) {
        this.drinkEffects = drinkEffects;
        return this;
    }

    /**
     * 药水喝下去会给什么状态效果
     * */
    public static class DrinkEffect {
        private final StatusEffect effect;
        private final float duration;       //持续时间，单位：秒
        private final int level;            //状态效果等级

        private DrinkEffect (StatusEffect effect, float duration, int level) {
            this.effect = effect;
            this.duration = duration;
            this.level = level;
        }

        /**
         * 创建一个
         * */
        public static PotionItem.DrinkEffect of (StatusEffect effect, float duration, int level) {
            return new PotionItem.DrinkEffect(effect, duration, level);
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
