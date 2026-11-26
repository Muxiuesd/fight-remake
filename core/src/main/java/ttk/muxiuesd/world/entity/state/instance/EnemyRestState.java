package ttk.muxiuesd.world.entity.state.instance;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.state.abs.StateEnemy;

/**
 * 敌人休息状态（通用）
 * */
public class EnemyRestState<T extends Enemy<?>> extends StateEnemy<T> {
    @Override
    public void start (World world, T entity) {
        //休息时清除速度，防止残留速度导致静止实体无限滑行
        entity.setVelocity(0, 0);
        //休息计时器
        setTimer(Pools.TASK_TIMER.obtain().setMaxSpan(MathUtils.random(1.8f, 3f)));
    }

    @Override
    public void handle (World world, T entity, float delta) {
        //检查一下是否有目标
        if (entity.checkTarget()) {
            entity.setState(Enemy.STATE_ATTACK_TARGET);
        } else {
            //没有目标就更新计时器
            updateAndCheckTimer(delta,
                () -> {
                    //计时器计时完毕
                    entity.setState(Enemy.STATE_RANDOM_WALK);
                },
                () -> {
                    //计时器还没好
                }
            );
        }
    }
}
