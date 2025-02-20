package ttk.muxiuesd.world.event.instance;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.system.EntitySystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Entity;
import ttk.muxiuesd.world.entity.enemy.Slime;
import ttk.muxiuesd.world.event.abs.EntityDeathEvent;

/**
 * 事件: 史莱姆死亡
 * */
public class EventSlimeDead extends EntityDeathEvent {

    private final World world;
    public int maxGeneration = 3;

    public EventSlimeDead (World world) {
        this.world = world;
    }

    @Override
    public void call (Entity deadEntity) {
        if (deadEntity instanceof Slime) {
            EntitySystem es = (EntitySystem) world.getSystemManager().getSystem("EntitySystem");
            Slime mom = (Slime) deadEntity;
            int generation = mom.generation;
            if (generation == maxGeneration) {
                return;
            }
            //Slime[] children = new Slime[MathUtils.random(generation, generation + 3)];
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
        }
    }
}
