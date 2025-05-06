package ttk.muxiuesd.world.event.instance;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.event.abs.EntityDeathEvent;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.system.SoundEffectSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.enemy.Slime;

/**
 * 事件: 史莱姆死亡
 * */
public class EventSlimeDead extends EntityDeathEvent {
    public int maxGeneration = 3;

    /*@Override
    public void callback (LivingEntity deadEntity) {
        //史莱姆死亡分裂
        if (deadEntity instanceof Slime) {
            EntitySystem es = (EntitySystem) world.getSystemManager().getSystem("EntitySystem");
            Slime mom = (Slime) deadEntity;
            int generation = mom.generation;
            if (generation == maxGeneration) {
                return;
            }
            for (int i = 0; i < MathUtils.random(generation, generation + 3); i++) {
                Slime child = new Slime();
                child.generation = generation + 1;
                child.setEntitySystem(es);
                float x = (float) (mom.x + MathUtils.random(mom.getWidth(), 2f) * Util.randomCos());
                float y = (float) (mom.y + MathUtils.random(mom.getHeight(), 2f) * Util.randomSin());
                float width = mom.getWidth() * 0.8f;
                float height = mom.getHeight() * 0.8f;
                child.setBounds(x, y, width, height);
                es.add(child);
            }
            SoundEffectSystem ses = (SoundEffectSystem) world.getSystemManager().getSystem("SoundEffectSystem");
            ses.newSpatialSound(Fight.getId("slime_small"), deadEntity);
        }
    }*/

    @Override
    public void handle (World world, LivingEntity entity) {
        //史莱姆死亡分裂
        if (entity instanceof Slime mom) {
            EntitySystem es = (EntitySystem) world.getSystemManager().getSystem("EntitySystem");
            int generation = mom.generation;
            if (generation == maxGeneration) {
                return;
            }
            for (int i = 0; i < MathUtils.random(generation, generation + 3); i++) {
                Slime child = new Slime();
                child.generation = generation + 1;
                child.setEntitySystem(es);
                float x = (float) (mom.x + MathUtils.random(mom.getWidth(), 2f) * Util.randomCos());
                float y = (float) (mom.y + MathUtils.random(mom.getHeight(), 2f) * Util.randomSin());
                float width = mom.getWidth() * 0.8f;
                float height = mom.getHeight() * 0.8f;
                child.setBounds(x, y, width, height);
                es.add(child);
            }
            SoundEffectSystem ses = (SoundEffectSystem) world.getSystemManager().getSystem("SoundEffectSystem");
            ses.newSpatialSound(Fight.getId("slime_small"), entity);
        }
    }
}
