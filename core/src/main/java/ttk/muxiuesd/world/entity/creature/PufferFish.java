package ttk.muxiuesd.world.entity.creature;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.Fight;
import ttk.muxiuesd.registry.*;
import ttk.muxiuesd.render.RenderLayer;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.entity.state.instance.PufferFishRandomWalkState;
import ttk.muxiuesd.world.entity.state.instance.PufferFishRestState;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 河豚
 * */
public class PufferFish extends LivingEntity<PufferFish> {
    public static final Vector2 DEFAULT_SIZE = Pools.VEC2.obtain().set(0.7f, 0.7f);
    public static final int MAX_RANDOM_COUNT = 5;

    /*private TaskTimer randomWalkTimer;  //随机游走计时器
    private Runnable walkTimerEndTask;  //游走结束任务
    private TaskTimer restTimer;    //休息计时器
    private Runnable restTimerEndTask;  //休息状态结束后任务*/

    private Vector2 walkDistance;

    public PufferFish() {
        initialize(EntityTypes.CREATURE, 5, 5, 1);
        loadBodyTextureRegion(Fight.getId("puffer_fish"), "fish/puffer_fish.png");
        setSize(DEFAULT_SIZE);
        setSpeed(1f);
        getBackpack().addItem(new ItemStack(Items.PUFFER_FISH, 1));

        /*this.restTimerEndTask = () -> {
            Pools.TASK_TIMER.free(this.restTimer);
            this.restTimer = null;
        };
        this.walkTimerEndTask =  () -> {
            Pools.TASK_TIMER.free(this.randomWalkTimer);
            this.randomWalkTimer = null;
            this.walkDistance = null;
            //每一次游走结束后生成休息计时器
            this.restTimer = Pools.TASK_TIMER.obtain().setMaxSpan(MathUtils.random(1f, 3f)).setTask(this.restTimerEndTask);
        };
        //实体刚生成不会马上游走
        this.restTimer = Pools.TASK_TIMER.obtain().setMaxSpan(MathUtils.random(1f, 3f)).setTask(this.restTimerEndTask);*/
        addState(Fight.getId("rest"), new PufferFishRestState());
        addState(Fight.getId("randomWalk"), new PufferFishRandomWalkState());
        setState(Fight.getId("rest"));
    }

    @Override
    public void update (float delta) {
        /*if (this.randomWalkTimer == null) {
            //没有游走计时器并且休息计时器准备好了就说明到了需要随机游走的时候
            if (this.restTimer != null && this.restTimer.isReady()) {
                this.randomWalkTimer = Pools.TASK_TIMER.obtain().setMaxSpan(2f).setTask(this.walkTimerEndTask);
                this.randomWalkPath(getEntitySystem().getWorld());
            }
        }else {
            //有timer就是在进行随机游走
            this.randomWalkTimer.update(delta);
            if (!this.randomWalkTimer.isReady()) {
                //设置速度
                setVelocity(this.walkDistance.x * delta * speed, walkDistance.y * delta * speed);
                setPosition(getPosition().add(velX, velY));
            }
        }
        if (this.restTimer != null) {
            this.restTimer.update(delta);
        }*/

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

    public Vector2 getWalkDistance () {
        return this.walkDistance;
    }

    public PufferFish setWalkDistance (Vector2 walkDistance) {
        this.walkDistance = walkDistance;
        return this;
    }

    @Override
    public RenderLayer getRenderLayer () {
        return RenderLayers.ENTITY_UNDERGROUND;
    }
}
