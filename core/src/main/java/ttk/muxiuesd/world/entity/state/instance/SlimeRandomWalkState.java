package ttk.muxiuesd.world.entity.state.instance;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.Enemy;
import ttk.muxiuesd.world.entity.enemy.Slime;
import ttk.muxiuesd.world.entity.state.abs.StateEnemy;

/**
 * slime随机游走状态
 * */
public class SlimeRandomWalkState extends StateEnemy<Slime> {
    private Vector2 walkDistance;

    @Override
    public void start (World world, Slime entity) {
        setTimer(Pools.TASK_TIMER.obtain().setMaxSpan(2f));
        this.randomWalkPath(world, entity);
    }

    @Override
    public void handle (World world, Slime entity, float delta) {
        updateAndCheckTimer(delta,
            ()->{
                //随机游走结束
                if (entity.checkTarget()) entity.setState(Slime.STATE_ATTACK_TARGET);
                else entity.setState(Slime.STATE_REST);
            },
            ()->{
                //还在游走状态;
                //设置速度（位移由 GroundEntityCollisionSystem 统一处理）
                //速度用基准速度（意图），实际速度由摩擦系统统一缩放
                //walkDistance 是位移矢量，需归一化成方向再乘速度，避免速度被放大 1.5~2.5 倍
                Vector2 direction = new Vector2(this.walkDistance).nor();
                entity.setVelocity(
                    direction.x * entity.getSpeed(),
                    direction.y * entity.getSpeed()
                );
            }
        );
    }

    /**
     * 生成简单的随机游走路线
     * */
    public void randomWalkPath (World world, Slime entity) {
        ChunkSystem cs = world.getSystem(ChunkSystem.class);
        Vector2 position = entity.getCenterPos();
        float dx = 0;
        float dy = 0;

        //随机生成路径
        for (int count = 0; count < Enemy.MAX_RANDOM_COUNT; count++) {
            double radian = Util.randomRadian();
            float distance = MathUtils.random(1.5f, 2.5f);
            float x = distance * MathUtils.cos((float) radian);
            float y = distance * MathUtils.sin((float) radian);
            //遇到不是水的方块直接确认路径
            if (cs.getBlock(position.x + x, position.y + y) != Blocks.WATER) {
                dx = x;
                dy = y;
                break;
            }
        }

        this.walkDistance = new Vector2().set(dx, dy);
    }
}
