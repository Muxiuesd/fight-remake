package ttk.muxiuesd.world.item.weapon.sword;

import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 构建一个剑类武器
 * */
public class SwordBuilder {
    /// 预先填一些默认值，防止null
    AudioHolder attackSound = Sounds.ENTITY_SWEEP;
    float attackRange   = 1.145f;   //攻击距离
    float damage        = 1.145f;   //攻击伤害
    float useSpan       = 1.145f;   //使用间隔
    int duration        = 1145;     //耐久值
    float knockback     = 2f;   //击退冲击力

    private SwordBuilder() {}

    public static SwordBuilder create () {
        return new SwordBuilder();
    }

    /**
     * 执行构建
     * */
    public Sword build () {
        return new Sword(
            createProperty()
                .setUseSound(this.attackSound)
                .add(PropertyTypes.WEAPON_ATTACK_RANGE, this.attackRange)
                .add(PropertyTypes.WEAPON_DAMAGE, this.damage)
                .add(PropertyTypes.WEAPON_USE_SAPN, this.useSpan)
                .add(PropertyTypes.ITEM_DURATION, this.duration)
                .add(PropertyTypes.WEAPON_KNOCKBACK, this.knockback)
        );
    }

    public static Item.Property createProperty () {
        return new Item.Property()
            .add(PropertyTypes.ITEM_MAX_COUNT, 1);
    }


    public SwordBuilder setAttackSound (AudioHolder attackSound) {
        if (attackSound != null) this.attackSound = attackSound;
        return this;
    }

    public SwordBuilder setAttackRange (float attackRange) {
        if (attackRange > 0f) this.attackRange = attackRange;
        return this;
    }

    public SwordBuilder setDamage (float damage) {
        if (damage >= 0f) this.damage = damage;
        return this;
    }

    public SwordBuilder setUseSpan (float useSpan) {
        if (useSpan >= 0f) this.useSpan = useSpan;
        return this;
    }

    public SwordBuilder setDuration (int duration) {
        if (duration >= 1) this.duration = duration;
        return this;
    }

    /**
     * 设置击退冲击力（0 = 不击退）
     * */
    public SwordBuilder setKnockback (float knockback) {
        if (knockback >= 0f) this.knockback = knockback;
        return this;
    }
}
