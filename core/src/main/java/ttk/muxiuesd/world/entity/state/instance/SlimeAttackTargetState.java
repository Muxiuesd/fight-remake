package ttk.muxiuesd.world.entity.state.instance;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.enemy.Slime;
import ttk.muxiuesd.world.entity.state.abs.StateEnemy;

/**
 * 史莱姆攻击状态
 * */
public class SlimeAttackTargetState extends StateEnemy<Slime> {
    @Override
    public void start (World world, Slime entity) {

    }

    @Override
    public void handle (World world, Slime entity, float delta) {
        //有目标
        if (entity.checkTarget()) {
            entity.walkToTarget(delta);
            entity.remoteAttack(delta);
        }else {
            //没目标就休息一下或者随机游走
            if (MathUtils.random(0, 1f) <= 0.7f) entity.setState(Slime.STATE_REST);
            else entity.setState(Slime.STATE_RANDOM_WALK);
        }
    }
}
