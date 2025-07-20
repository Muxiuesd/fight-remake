package ttk.muxiuesd.world.entity.state.instance;

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
        super(Pools.TASK_TIMER.obtain().setMaxSpan(2f), () -> {});
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
                entity.setVelocity(walkDistance.x * delta * entity.speed, walkDistance.y * delta * entity.speed);
                entity.setPosition(entity.getPosition().add(entity.getVelocity()));
            }
        }
    }
}
