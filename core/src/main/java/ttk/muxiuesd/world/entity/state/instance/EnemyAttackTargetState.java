package ttk.muxiuesd.world.entity.state.instance;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.state.abs.StateEnemy;

/**
 * 敌人攻击状态（通用）
 * <p>
 * 攻击方式由敌人覆写的 {@link Enemy#attack} 决定（近战/远程）
 * */
public class EnemyAttackTargetState<T extends Enemy<?>> extends StateEnemy<T> {
    @Override
    public void start (World world, T entity) {

    }

    @Override
    public void handle (World world, T entity, float delta) {
        //有目标
        if (entity.checkTarget()) {
            entity.walkToTarget(entity.getCurTarget());
            entity.attack(delta);
        }else {
            //没目标就休息一下或者随机游走
            if (MathUtils.random(0, 1f) <= 0.7f) entity.setState(Enemy.STATE_REST);
            else entity.setState(Enemy.STATE_RANDOM_WALK);
        }
    }
}
