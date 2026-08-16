package ttk.muxiuesd.world.entity.genfactory;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.interfaces.world.entity.CreatureGenFactory;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.registry.Entities;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.entity.creature.PufferFish;

/**
 * 河豚的生成工厂
 * */
public class PufferFishGenFactory implements CreatureGenFactory<PufferFish> {
    public static final int ONCE_MAX_GAN = 3;

    /**
     * 河豚只能生成在水里（必须站在水方块上；水中无墙，无需膨胀）
     */
    @Override
    public boolean isValidGenPos (World world, float genX, float genY) {
        ChunkSystem cs = world.getSystem(ChunkSystem.class);
        //区块已加载 + 方块是水（河豚可游泳）
        return cs.getChunk(genX, genY) != null
            && cs.getBlock(genX, genY) == Blocks.WATER;
    }

    @Override
    public PufferFish[] create (World world, float genX, float genY) {
        PufferFish[] fish = new PufferFish[MathUtils.random(1, ONCE_MAX_GAN)];
        for (int i = 0; i < fish.length; i++) {
            PufferFish pufferFish = Entities.PUFFER_FISH.create(world);
            pufferFish.setPosition(genX, genY);
            fish[i] = pufferFish;
        }
        return fish;
    }
}
