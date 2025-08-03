package ttk.muxiuesd.world.entity.state.instance;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.creature.PufferFish;
import ttk.muxiuesd.world.entity.state.abs.StatePufferFish;

/**
 * 河豚休息状态
 * */
public class PufferFishRestState extends StatePufferFish{

    public PufferFishRestState () {
    }

    @Override
    public void start (World world, PufferFish entity) {
        setTimer(Pools.TASK_TIMER.obtain().setMaxSpan(MathUtils.random(1f, 3f)));
    }

    @Override
    public void handle (World world, PufferFish entity, float delta) {
        TaskTimer timer = getTimer();
        if (timer != null) {
            timer.update(delta);
            if (timer.isReady()) {
                entity.randomWalkPath(world);
                entity.setState(Fight.getId("random_walk"));
            }
        }
    }

    @Override
    public void end (World world, PufferFish entity) {
        Pools.TASK_TIMER.free(getTimer());
        setTimer(null);
    }
}
