package ttk.muxiuesd.world.entity.state.instance;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.creature.PufferFish;
import ttk.muxiuesd.world.entity.state.abs.StatePufferFish;

/**
 * 河豚随机游走状态
 * */
public class PufferFishRandomWalkState extends StatePufferFish {

    public PufferFishRandomWalkState () {
    }

    @Override
    public void start (World world, PufferFish entity) {
        setTimer(Pools.TASK_TIMER.obtain().setMaxSpan(MathUtils.random(1.2f, 2.7f)));
    }

    @Override
    public void handle (World world, PufferFish entity, float delta) {
        TaskTimer timer = getTimer();
        if (timer != null) {
            timer.update(delta);
            if (timer.isReady()) {
                entity.setState(Fight.getId("rest"));
            }else {
                //还在游走状态
                Vector2 walkDistance = entity.getWalkDistance();
                //设置速度
                entity.setVelocity(walkDistance.x * entity.speed, walkDistance.y * entity.speed);
                entity.setPosition(entity.getPosition().add(entity.getVelocity().scl(delta)));
            }
        }
    }

    @Override
    public void end (World world, PufferFish entity) {
        Pools.TASK_TIMER.free(getTimer());
        setTimer(null);
    }
}
