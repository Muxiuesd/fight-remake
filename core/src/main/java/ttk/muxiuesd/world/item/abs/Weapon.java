package ttk.muxiuesd.world.item.abs;

import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.LivingEntity;

/**
 * 武器类
 * */
public abstract class Weapon extends Item{
    public Weapon (WeaponProperties property, String textureId, String texturePath) {
        super(Type.WEAPON, property, textureId, texturePath);
    }

    @Override
    public boolean use (World world, LivingEntity user) {
        return super.use(world, user);
    }

    /**
     * 武器属性
     */
    public static class WeaponProperties extends Item.Property {
        private float damage = 1f;   //基础伤害
        private int duration = 100; //耐久度
        private float useSpan = 1f;  //使用间隔

        public WeaponProperties () {
            //默认武器最大只能堆叠一个
            setMaxCount(1);
        }

        public float getDamage () {
            return this.damage;
        }

        public WeaponProperties setDamage (float damage) {
            this.damage = damage;
            return this;
        }

        public int getDuration () {
            return this.duration;
        }

        public WeaponProperties setDuration (int duration) {
            this.duration = duration;
            return this;
        }

        public float getUseSpan () {
            return this.useSpan;
        }

        public WeaponProperties setUseSpan (float useSpan) {
            this.useSpan = useSpan;
            return this;
        }
    }
}
