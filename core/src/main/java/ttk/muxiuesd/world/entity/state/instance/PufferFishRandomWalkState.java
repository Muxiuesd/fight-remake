package ttk.muxiuesd.world.entity.state.instance;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.util.TaskTimer;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Pools;
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
                entity.setState(Fight.ID("rest"));
            }else {
                //还在游走状态
                Vector2 walkDistance = entity.getWalkDistance();
                //设置速度（位移由 GroundEntityCollisionSystem 统一处理）
                float speed = entity.getSpeed();
                //walkDistance 是位移矢量，需归一化成方向再乘速度
                Vector2 direction = new Vector2(walkDistance).nor();
                entity.setVelocity(direction.x * speed, direction.y * speed);
            }
        }
    }

    @Override
    public void end (World world, PufferFish entity) {
        Pools.TASK_TIMER.free(getTimer());
        setTimer(null);
    }
}
