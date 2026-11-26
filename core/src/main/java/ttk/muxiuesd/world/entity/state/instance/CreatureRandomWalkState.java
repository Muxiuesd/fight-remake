package ttk.muxiuesd.world.entity.state.instance;

import com.badlogic.gdx.math.Vector2;
import game.muxiuesd.bedrockcore.util.TaskTimer;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.CreatureEntity;
import ttk.muxiuesd.world.entity.state.abs.StateCreature;

/**
 * 生物随机游走状态
 * <p>
 * 沿预先算好的随机路线移动，游走结束后回到休息状态
 */
public class CreatureRandomWalkState<T extends CreatureEntity<?>> extends StateCreature<T> {

    @Override
    public void start (World world, T entity) {
        setTimer(Pools.TASK_TIMER.obtain().setMaxSpan(2f));
    }

    @Override
    public void handle (World world, T entity, float delta) {
        TaskTimer timer = this.getTimer();
        if (timer != null) {
            timer.update(delta);
            if (timer.isReady()) {
                //游走结束，回去休息
                entity.setState(CreatureEntity.STATE_REST);
            } else {
                //还在游走状态，沿随机路线移动（位移由 GroundEntityCollisionSystem 统一处理）
                Vector2 walkDistance = entity.getWalkDistance();
                float speed = entity.getSpeed();
                if (walkDistance != null) {
                    //walkDistance 是位移矢量，需归一化成方向再乘速度
                    Vector2 direction = new Vector2(walkDistance).nor();
                    entity.setVelocity(direction.x * speed, direction.y * speed);
                }
            }
        }
    }

    @Override
    public void end (World world, T entity) {
        Pools.TASK_TIMER.free(this.getTimer());
        setTimer(null);
    }
}
