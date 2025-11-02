package ttk.muxiuesd.world.entity.state.instance;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.creature.PufferFish;
import ttk.muxiuesd.world.entity.state.abs.StatePufferFish;

/**
 * 河豚休息状态
 * */
public class PufferFishRestState extends StatePufferFish{
    public static final int MAX_RANDOM_COUNT = 5;   //最大随机次数
    public PufferFishRestState () {
    }

    @Override
    public void start (World world, PufferFish entity) {
        setTimer(Pools.TASK_TIMER.obtain().setMaxSpan(MathUtils.random(1f, 3f)));
    }

    @Override
    public void handle (World world, PufferFish entity, float delta) {
        TaskTimer timer = getTimer();
        if (timer != null) {
            timer.update(delta);
            if (timer.isReady()) {
                this.randomWalkPath(world, entity);
                entity.setState(Fight.ID("random_walk"));
            }
        }
    }

    @Override
    public void end (World world, PufferFish entity) {
        Pools.TASK_TIMER.free(getTimer());
        setTimer(null);
    }

    /**
     * 生成简单的随机游走路线
     * */
    public void randomWalkPath (World world, PufferFish entity) {
        ChunkSystem cs = world.getSystem(ChunkSystem.class);
        Vector2 position = entity.getCenter();
        float dx = 0;
        float dy = 0;

        //随机路线
        for (int count = 0; count < MAX_RANDOM_COUNT; count++) {
            double radian = Util.randomRadian();
            float distance = MathUtils.random(0.5f, 1.5f);
            float x = distance * MathUtils.cos((float) radian);
            float y = distance * MathUtils.sin((float) radian);
            //随机到水方块直接确认路径
            if (cs.getBlock(position.x + x, position.y + y) == Blocks.WATER) {
                dx = x;
                dy = y;
                break;
            }
        }

        entity.setWalkDistance(new Vector2().set(dx, dy));
    }
}
