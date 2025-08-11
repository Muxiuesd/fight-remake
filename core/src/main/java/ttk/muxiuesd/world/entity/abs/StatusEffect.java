package ttk.muxiuesd.world.entity.abs;

/**
 * 状态效果抽象类
 * */
public abstract class StatusEffect {
    private final String id;

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


    /**
     * 状态效果的数据类
     * */
    public static class Data {
        //持续时间，单位：秒
        private float duration;
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
}
