package ttk.muxiuesd.world.item.weapon;

import ttk.muxiuesd.audio.AudioHolder;
import ttk.muxiuesd.interfaces.world.entity.BulletFactory;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.world.item.abs.Item;
import ttk.muxiuesd.world.item.builder.ItemBuilder;

/**
 * 远程武器构建器
 * <p>
 * 替代原先具体的远程武器子类，子弹发射逻辑通过 {@link BulletFactory} 注入，
 * 武器属性通过字段 setter 定制。
 */
public class RangedWeaponBuilder implements ItemBuilder<RangedWeapon> {
    /// 预先填一些默认值，防止null
    private AudioHolder shootSound = Sounds.ENTITY_SHOOT;
    private float damage = 1f;        //攻击伤害
    private float useSpan = 1f;       //使用间隔
    private int duration = 100;       //耐久值
    private float knockback = 2f;     //击退冲击力
    private BulletFactory<?> factory; //子弹的工厂实现类

    private RangedWeaponBuilder () {
    }

    public static RangedWeaponBuilder create () {
        return new RangedWeaponBuilder();
    }

    public RangedWeaponBuilder setShootSound (AudioHolder shootSound) {
        if (shootSound != null) this.shootSound = shootSound;
        return this;
    }

    public RangedWeaponBuilder setDamage (float damage) {
        if (damage >= 0f) this.damage = damage;
        return this;
    }

    public RangedWeaponBuilder setUseSpan (float useSpan) {
        if (useSpan >= 0f) this.useSpan = useSpan;
        return this;
    }

    public RangedWeaponBuilder setDuration (int duration) {
        if (duration >= 1) this.duration = duration;
        return this;
    }

    /**
     * 设置击退冲击力（0 = 不击退）
     * */
    public RangedWeaponBuilder setKnockback (float knockback) {
        if (knockback >= 0f) this.knockback = knockback;
        return this;
    }

    public RangedWeaponBuilder setFactory (BulletFactory<?> factory) {
        this.factory = factory;
        return this;
    }

    @Override
    public RangedWeapon build () {
        Item.Property property = RangedWeapon.createDefaultProperty()
            .setUseSound(this.shootSound)
            .add(PropertyTypes.WEAPON_DAMAGE, this.damage)
            .add(PropertyTypes.WEAPON_USE_SAPN, this.useSpan)
            .add(PropertyTypes.ITEM_DURATION, this.duration)
            .add(PropertyTypes.WEAPON_KNOCKBACK, this.knockback);
        return new RangedWeapon(property, this.factory);
    }
}