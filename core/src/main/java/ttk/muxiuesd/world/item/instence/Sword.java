package ttk.muxiuesd.world.item.instence;

import com.badlogic.gdx.utils.Array;
import ttk.muxiuesd.event.EventBus;
import ttk.muxiuesd.event.EventTypes;
import ttk.muxiuesd.event.poster.EventPosterEntityHurt;
import ttk.muxiuesd.registry.DamageTypes;
import ttk.muxiuesd.registry.PropertyTypes;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.system.SoundEffectSystem;
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
        return new Property().setPropertiesMap(DEFAULT_WEAPON_PROPERTIES_DATA_MAP.copy())
            .add(PropertyTypes.WEAPON_ATTACK_RANGE, 2.5f)
            .setUseSoundId(Sounds.ENTITY_SWEEP.getId());
    }

    public Sword (Property property, String textureId, String texturePath) {
        super(property, textureId, texturePath);
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity user) {
        Float range = itemStack.getProperty().get(PropertyTypes.WEAPON_ATTACK_RANGE);
        EntitySystem es = (EntitySystem) world.getSystemManager().getSystem("EntitySystem");
        //检测剑的伤害区域内的敌人实体
        Array<Enemy> entities = Util.sectorArea(es.enemyEntity, user.getCenter(), user.getDirection(), range, 60f);
        for (Enemy enemy : entities) {
            enemy.applyDamage(DamageTypes.SWORD, user);
            //发送事件
            EventBus.post(EventTypes.ENTITY_HURT, new EventPosterEntityHurt(world, user, enemy));
        }

        String useSoundId = this.property.getUseSoundId();
        SoundEffectSystem ses = (SoundEffectSystem)world.getSystemManager().getSystem("SoundEffectSystem");
        ses.newSpatialSound(useSoundId, user);
        return true;
    }
}
