package ttk.muxiuesd.world.item.instence;

import ttk.muxiuesd.system.HandleInputSystem;
import ttk.muxiuesd.util.BlockPosition;
import ttk.muxiuesd.world.World;
import ttk.muxiuesd.world.block.abs.Block;
import ttk.muxiuesd.world.entity.LivingEntity;
import ttk.muxiuesd.world.item.abs.Item;

/**
 * 方块物品
 * */
public class BlockItem extends Item {
    final Block block;

    public BlockItem(Block block, String textureId) {
        this(block, new Property().setMaxCount(64), textureId);
    }

    public BlockItem(Block block, Property property, String textureId) {
        super(Type.CONSUMPTION, property, textureId);
        this.block = block;
    }

    @Override
    public boolean use (World world, LivingEntity user) {
        HandleInputSystem his = (HandleInputSystem) world.getSystemManager().getSystem("HandleInputSystem");
        BlockPosition position = his.getMouseBlockPosition();


        super.use(world, user);
        return true;
    }
}
