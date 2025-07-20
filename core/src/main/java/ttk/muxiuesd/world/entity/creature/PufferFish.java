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

    private Vector2 walkDistance;

    public PufferFish() {
        super(EntityTypes.CREATURE, 5, 5, 1);
        loadBodyTextureRegion(Fight.getId("puffer_fish"), "fish/puffer_fish.png");
        setSize(DEFAULT_SIZE);
        setSpeed(1f);
        getBackpack().addItem(new ItemStack(Items.PUFFER_FISH, 1));

        addState(Fight.getId("rest"), new PufferFishRestState());
        addState(Fight.getId("random_walk"), new PufferFishRandomWalkState());
    }

    @Override
    public void initialize () {
        setState(Fight.getId("rest"));
    }

    @Override
    public void update (float delta) {
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
