package ttk.muxiuesd.world.entity.creature;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.registry.Items;
import ttk.muxiuesd.registry.RenderLayers;
import ttk.muxiuesd.render.RenderLayer;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.EntityType;
import ttk.muxiuesd.world.entity.abs.CreatureEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 河豚
 * <p>
 * 水生生物实体，受击会本能远离攻击者；
 * 随机游走偏好趋水（覆写 {@link #randomWalkPath}）
 * */
public class PufferFish extends CreatureEntity<PufferFish> {
    public static final Vector2 DEFAULT_SIZE = new Vector2(0.7f, 0.7f);

    public PufferFish (World world, EntityType<? super PufferFish> entityType) {
        super(world, entityType, 5, 5, 1);
        setSize(DEFAULT_SIZE);
        fastAddBodyHitBox();
        setSpeed(1f);
        getBackpack().addItem(new ItemStack(Items.PUFFER_FISH, 1));
    }

    /**
     * 河豚是水生生物，可以游泳（寻路时水方块视为可走）
     */
    @Override
    public boolean canSwim () {
        return true;
    }

    /**
     * 河豚的随机游走偏好：往水里面游
     */
    @Override
    public void randomWalkPath (World world, float maxDistance) {
        ChunkSystem cs = world.getSystem(ChunkSystem.class);
        Vector2 position = this.getCenterPos();
        float dx = 0;
        float dy = 0;

        //随机生成路线，河豚倾向往水里游
        for (int count = 0; count < MAX_RANDOM_COUNT; count++) {
            double radian = Util.randomRadian();
            float distance = MathUtils.random(maxDistance / 2f, maxDistance);
            float x = distance * MathUtils.cos((float) radian);
            float y = distance * MathUtils.sin((float) radian);
            //目标点是水就确认路径
            if (cs.getBlock(position.x + x, position.y + y) == Blocks.WATER) {
                dx = x;
                dy = y;
                break;
            }
        }

        this.setWalkDistance(new Vector2().set(dx, dy));
    }

    @Override
    public RenderLayer getRenderLayer () {
        return RenderLayers.ENTITY_UNDERGROUND;
    }
}
