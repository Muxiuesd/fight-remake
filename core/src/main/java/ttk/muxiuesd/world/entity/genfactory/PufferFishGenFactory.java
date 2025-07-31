package ttk.muxiuesd.world.entity.genfactory;

import com.badlogic.gdx.math.MathUtils;
import ttk.muxiuesd.interfaces.world.entity.CreatureGenFactory;
import ttk.muxiuesd.registry.Blocks;
import ttk.muxiuesd.registry.Entities;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.creature.PufferFish;

/**
 * 河豚的生成工厂
 * */
public class PufferFishGenFactory implements CreatureGenFactory<PufferFish> {
    public static final int ONCE_MAX_GAN = 3;
    @Override
    public PufferFish[] create (World world, float genX, float genY) {
        ChunkSystem cs = (ChunkSystem) world.getSystemManager().getSystem("ChunkSystem");
        Block block = cs.getBlock(genX, genY);
        //生成的位置不是水就直接跳过
        if (block != Blocks.WATER) return null;

        PufferFish[] fish = new PufferFish[MathUtils.random(1, ONCE_MAX_GAN)];
        for (int i = 0; i < fish.length; i++) {
            PufferFish pufferFish = Entities.PUFFER_FISH.create(world);
            pufferFish.setPosition(genX, genY);
            fish[i] = pufferFish;
        }
        return fish;
    }
}
