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

    private TaskTimer timer;
    private Vector2 targetDistance;

    public PufferFish() {
        initialize(Group.creature, 5, 5, 1);
        loadBodyTextureRegion(Fight.getId("puffer_fish"), "fish/puffer_fish.png");
        setSize(DEFAULT_SIZE);
        setSpeed(1f);
    }

    @Override
    public void update (float delta) {
        super.update(delta);
        if (this.timer == null) {
            //没有timer就说明没有随机游走，就生成一个
            this.timer = new TaskTimer(2f, () -> {
                this.timer = null;
                this.targetDistance = null;
            });
            this.randomWalkPath(getEntitySystem().getWorld());
        }else {
            //有timer就是在进行随机游走
            this.timer.update(delta);
            if (!this.timer.isReady()) {
                setPosition(getPosition().add(
                    this.targetDistance.x * delta * speed,
                    this.targetDistance.y * delta * speed)
                );
            }
        }

    }

    /**
     * 简单的随机游走路线
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
        this.targetDistance = new Vector2().set(dx, dy);
    }


    @Override
    public RenderLayer getRenderLayer () {
        return RenderLayers.ENTITY_UNDERGROUND;
    }
}
