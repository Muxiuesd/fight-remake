package ttk.muxiuesd.world.item.weapon;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterEntityHurt;
import ttk.muxiuesd.interfaces.world.item.IItemStackBehaviour;
import ttk.muxiuesd.registry.*;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.ui.text.Text;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;
import ttk.muxiuesd.world.item.abs.Weapon;

/**
 * 剑一类的近战武器
 * */
public class Sword extends Weapon {
    /**
     * 新建一个默认的近战武器的属性
     */
    public static Property createDefaultProperty() {
        return Weapon.createDefaultProperty()
            .add(PropertyTypes.WEAPON_ATTACK_RANGE, 2.5f)
            .setUseSoundId(Sounds.ENTITY_SWEEP.getId());
    }

    public Sword (Property property, String textureId, String texturePath) {
        super(property, textureId, texturePath);
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        Float range = itemStack.getProperty().get(PropertyTypes.WEAPON_ATTACK_RANGE);
        EntitySystem es = world.getSystem(EntitySystem.class);
        //检测剑的伤害区域内的敌人实体
        Array<Enemy<?>> entities = Util.sectorArea(es.getEnemyEntity(), user.getCenter(), user.getDirection(), range, 60f);
        for (Enemy<?> enemy : entities) {
            enemy.applyDamage(DamageTypes.SWORD, user);
            //发送事件
            EventBus.post(EventTypes.ENTITY_HURT, new EventPosterEntityHurt(world, user, enemy));
        }
        //检测剑的伤害区域内的生物实体
        Array<LivingEntity<?>> livingEntities = Util.sectorArea(es.getEntityArray(EntityTypes.CREATURE), user.getCenter(), user.getDirection(), range, 60f);
        for (LivingEntity<?> le : livingEntities) {
            le.applyDamage(DamageTypes.SWORD, user);
            //发送事件
            EventBus.post(EventTypes.ENTITY_HURT, new EventPosterEntityHurt(world, user, le));
        }

        String useSoundId = this.property.getUseSoundId();
        SoundEffectSystem ses = world.getSystem(SoundEffectSystem.class);
        ses.newSpatialSound(useSoundId, user);
        return true;
    }

    @Override
    public Array<Text> getTooltips (Array<Text> array, ItemStack itemStack) {
        array.add(Text.of("攻击距离：" + itemStack.getProperty().get(PropertyTypes.WEAPON_ATTACK_RANGE)));
        array.add(Text.of("攻击间隔：" + itemStack.getProperty().get(PropertyTypes.WEAPON_USE_SAPN)));
        array.add(Text.of("攻击伤害：" + itemStack.getProperty().get(PropertyTypes.WEAPON_DAMAGE)));
        return array;
    }

    @Override
    public IItemStackBehaviour getBehaviour () {
        return ItemStackBehaviours.SWORD;
    }
}
