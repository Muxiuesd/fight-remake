package ttk.muxiuesd.world.entity.abs;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ttk.muxiuesd.util.Util;

/**
 * 状态效果抽象类
 * */
public abstract class StatusEffect {
    private final String id;
    private TextureRegion icon;

    public StatusEffect (String id) {
        this.id = id;
    }

    /**
     * 每tick对实体触发的效果
     *
     * @param entity 作用效果的实体
     * @param level 当前效果的等级
     * */
    public abstract void applyEffectTick(LivingEntity<?> entity, int level);


    public String getId () {
        return this.id;
    }

    public StatusEffect loadIcon (String textureId, String path) {
        this.icon = Util.loadTextureRegion(textureId, path);
        return this;
    }

    public TextureRegion getIcon () {
        return this.icon;
    }

    /**
     * 状态效果的数据类
     * */
    public static class Data {
        //持续时间，单位：秒
        private float duration;
        //状态效果等级
        private int level;

        public Data (float duration, int level) {
            this.duration = duration;
            this.level = level;
        }

        public float getDuration () {
            return this.duration;
        }

        public Data setDuration (float duration) {
            this.duration = duration;
            return this;
        }

        public Data decreaseDuration (float decrease) {
            this.duration -= decrease;
            return this;
        }

        public int getLevel () {
            return this.level;
        }

        public Data setLevel (int level) {
            this.level = level;
            return this;
        }
    }

    /**
     * 状态效果伤害来源
     * <P>
     * 如果是造成伤害的状态效果，就实现这个来应用伤害
     * */
    public static abstract class DamageSource {
        private final int level;

        protected DamageSource (int level) {
            this.level = level;
        }

        /**
         * 获取伤害
         * */
        public abstract float getDamage (LivingEntity<?> victim);

        public int getLevel () {
            return this.level;
        }
    }
}
