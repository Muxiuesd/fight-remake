package ttk.muxiuesd.world.entity.creature;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.registry.Pools;
import ttk.muxiuesd.registry.RenderLayers;
import ttk.muxiuesd.render.RenderLayer;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.TaskTimer;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.Group;
import ttk.muxiuesd.world.entity.abs.LivingEntity;

/**
 * 河豚
 * */
public class PufferFish extends LivingEntity {
    public static final Vector2 DEFAULT_SIZE = Pools.VEC2.obtain().set(0.7f, 0.7f);
    public static final int MAX_RANDOM_COUNT = 5;

    private TaskTimer randomWalkTimer;  //随机游走计时器
    private Vector2 walkDistance;
    private Runnable walkTimerEndTask;  //游走结束任务
    private TaskTimer restTimer;    //休息计时器
    private Runnable restTimerEndTask;

    public PufferFish() {
        initialize(Group.creature, 5, 5, 1);
        loadBodyTextureRegion(Fight.getId("puffer_fish"), "fish/puffer_fish.png");
        setSize(DEFAULT_SIZE);
        setSpeed(1f);

        this.restTimerEndTask = () -> this.restTimer = null;
        this.walkTimerEndTask =  () -> {
            this.randomWalkTimer = null;
            this.walkDistance = null;
            //每一次游走结束后生成休息计时器
            this.restTimer = new TaskTimer(MathUtils.random(1f, 3f), this.restTimerEndTask);
        };
        //实体刚生成不会马上游走
        this.restTimer = new TaskTimer(MathUtils.random(1f, 3f), this.restTimerEndTask);
    }

    @Override
    public void update (float delta) {
        if (this.randomWalkTimer == null) {
            //没有游走计时器并且休息计时器准备好了就说明到了需要随机游走得时候
            if (this.restTimer != null && this.restTimer.isReady()) {
                this.randomWalkTimer = new TaskTimer(2f, this.walkTimerEndTask);
                this.randomWalkPath(getEntitySystem().getWorld());
            }
        }else {
            //有timer就是在进行随机游走
            this.randomWalkTimer.update(delta);
            if (!this.randomWalkTimer.isReady()) {
                setPosition(getPosition().add(
                    this.walkDistance.x * delta * speed,
                    this.walkDistance.y * delta * speed)
                );
            }
        }
        if (this.restTimer != null) {
            this.restTimer.update(delta);
        }

        super.update(delta);
    }

    /**
     * 生成简单的随机游走路线
     * */
    public void randomWalkPath (World world) {
        ChunkSystem cs = (ChunkSystem) world.getSystemManager().getSystem("ChunkSystem");
        Vector2 position = getCenter();
        float dx;
        float dy;
        int count = 0;
        //随机路线
        do {
            double radian = Util.randomRadian();
            float distance = MathUtils.random(0.5f, 1.5f);
            dx = distance * MathUtils.cos((float) radian);
            dy = distance * MathUtils.sin((float) radian);
            count++;
            //目的地的坐标的方块得是水方块
        }while (cs.getBlock(position.x + dx, position.y + dy) != Blocks.WATER || count == MAX_RANDOM_COUNT);
        this.walkDistance = new Vector2().set(dx, dy);
    }


    @Override
    public RenderLayer getRenderLayer () {
        return RenderLayers.ENTITY_UNDERGROUND;
    }
}
