package ttk.muxiuesd.world.item.consumption;

import com.badlogic.gdx.math.Vector2;
import ttk.muxiuesd.system.ChunkSystem;
import ttk.muxiuesd.system.SoundSystem;
import ttk.muxiuesd.util.Util;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.abs.LivingEntity;
import ttk.muxiuesd.world.item.ItemStack;

/**
 * 方块物品
 * <p>
 * 贴图直接使用方块的贴图（id 与方块一致，通过已注册的 id→路径映射获取）
 * */
public class BlockItem extends ConsumptionItem {
    final Block block;

    public BlockItem(Block block) {
        this(block, new Property().setMaxCount(64));
    }

    public BlockItem(Block block, Property property) {
        super(property);
        this.block = block;
    }

    @Override
    public boolean use (ItemStack itemStack, World world, LivingEntity<?> user) {
        Vector2 worldPosition = Util.getMouseWorldPosition();
        ChunkSystem cs = world.getSystem(ChunkSystem.class);

        //放置方块：这一格原本的旧方块由 ChunkSystem 统一收尾（破坏音 + 残渣粒子 + 掉落）
        //放不上来（区块没加载）就返回false，物品不会被消耗
        if (!cs.placeBlock(this.block, worldPosition.x, worldPosition.y)) return false;

        //播放放置音效：用方块自己的放置音（物品默认的 use 不播音效也不报成功，这里得自己来）
        world.getSystem(SoundSystem.class).playPutSound(this.block, worldPosition.x, worldPosition.y);
        return true;
    }
}
