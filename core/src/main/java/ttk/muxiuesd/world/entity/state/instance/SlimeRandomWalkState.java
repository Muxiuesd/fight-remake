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
        setTimer(Pools.TASK_TIMER.obtain().setMaxSpan(5f));
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
                //设置速度
                float curSpeed = entity.getCurSpeed();
                entity.setVelocity(
                    this.walkDistance.x * curSpeed,
                    this.walkDistance.y * curSpeed
                );
                entity.setPosition(entity.getPosition().add(entity.getVelocity().scl(delta)));
            }
        );
    }

    /**
     * 生成简单的随机游走路线
     * */
    public void randomWalkPath (World world, Slime entity) {
        ChunkSystem cs = world.getSystem(ChunkSystem.class);
        Vector2 position = entity.getCenter();
        float dx;
        float dy;
        //随机次数
        int count = 0;
        //随机路线
        do {
            double radian = Util.randomRadian();
            float distance = MathUtils.random(1.5f, 2.5f);
            dx = distance * MathUtils.cos((float) radian);
            dy = distance * MathUtils.sin((float) radian);
            count++;
            //不会主动游走到水方块上
        }while (
            cs.getBlock(position.x + dx, position.y + dy) == Blocks.WATER
            || count <= Enemy.MAX_RANDOM_COUNT
        );

        this.walkDistance = new Vector2().set(dx, dy);
    }
}
