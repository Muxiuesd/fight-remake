package ttk.muxiuesd.world.event;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.event.abs.LivingEntityDeathEvent;
import ttk.muxiuesd.registry.Entities;
import ttk.muxiuesd.registry.Sounds;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.util.Info;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.enemy.Slime;

/**
 * 事件: 史莱姆死亡
 * */
public class EventSlimeDead extends LivingEntityDeathEvent {
    public static final Info<Integer> MAX_GENERATION = Info.create("slime_max_generation", 3);

    @Override
    public void handle (World world, LivingEntity<?> entity) {
        //史莱姆死亡分裂
        if (entity instanceof Slime mom) {
            EntitySystem es = world.getSystem(EntitySystem.class);
            int generation = mom.generation;

            if (generation >= MAX_GENERATION.getValue()) return;

            for (int i = 0; i < MathUtils.random(generation, generation + 2); i++) {
                Slime child = Entities.SLIME.create(world);
                child.generation = generation + 1;
                float x = (float) (mom.x + MathUtils.random(mom.getWidth(), 2f) * Util.randomCos());
                float y = (float) (mom.y + MathUtils.random(mom.getHeight(), 2f) * Util.randomSin());
                float width = mom.getWidth() * 0.8f;
                float height = mom.getHeight() * 0.8f;
                child.setBounds(x, y, width, height);
                child.setEntitySystem(es);
                es.add(child);
            }
            SoundEffectSystem ses = world.getSystem(SoundEffectSystem.class);
            ses.newSpatialSound(Sounds.ENTITY_SLIME_SMALL, entity);
        }
    }
}
