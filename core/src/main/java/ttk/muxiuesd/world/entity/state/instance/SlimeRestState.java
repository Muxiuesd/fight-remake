package ttk.muxiuesd.world.entity.state.instance;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.enemy.Slime;
import ttk.muxiuesd.world.entity.state.abs.StateEnemy;

/**
 * slime休息状态
 * */
public class SlimeRestState extends StateEnemy<Slime> {
    @Override
    public void start (World world, Slime entity) {
        //休息计时器
        setTimer(Pools.TASK_TIMER.obtain().setMaxSpan(MathUtils.random(1.8f, 3f)));
    }

    @Override
    public void handle (World world, Slime entity, float delta) {
        //检查一下是否有目标
        if (entity.checkTarget()) {
            entity.setState(Slime.STATE_ATTACK_TARGET);
        } else {
            //没有目标就更新计时器
            updateAndCheckTimer(delta,
                () -> {
                    //计时器计时完毕
                    entity.setState(Slime.STATE_RANDOM_WALK);
                },
                () -> {
                    //计时器还没好
                }
            );
        }
    }
}
