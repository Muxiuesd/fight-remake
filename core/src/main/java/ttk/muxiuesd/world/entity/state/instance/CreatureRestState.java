package ttk.muxiuesd.world.entity.state.instance;

import com.badlogic.gdx.math.MathUtils;
import game.muxiuesd.bedrockcore.util.TaskTimer;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.CreatureEntity;
import ttk.muxiuesd.world.entity.state.abs.StateCreature;

/**
 * 生物休息状态
 * <p>
 * 休息一段时间后随机游走
 */
public class CreatureRestState<T extends CreatureEntity<?>> extends StateCreature<T> {

    @Override
    public void start (World world, T entity) {
        //休息时清除速度，防止残留速度导致静止实体漂移
        entity.setVelocity(0, 0);
        setTimer(Pools.TASK_TIMER.obtain().setMaxSpan(MathUtils.random(1f, 3f)));
    }

    @Override
    public void handle (World world, T entity, float delta) {
        TaskTimer timer = this.getTimer();
        if (timer != null) {
            timer.update(delta);
            if (timer.isReady()) {
                //休息完毕，生成随机游走路线
                entity.randomWalkPath(world, 2.5f);
                entity.setState(CreatureEntity.STATE_RANDOM_WALK);
            }
        }
    }

    @Override
    public void end (World world, T entity) {
        Pools.TASK_TIMER.free(this.getTimer());
        setTimer(null);
    }
}
